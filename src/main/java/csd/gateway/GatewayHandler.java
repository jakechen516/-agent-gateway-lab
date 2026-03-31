package csd.gateway;

import csd.mock.TokenGenerator;
import csd.protocol.Envelope;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket message handler for Agent Gateway protocol.
 * [PROTO] Handles START -> TOKEN* -> DONE flow.
 */
public class GatewayHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static final Logger log = LoggerFactory.getLogger(GatewayHandler.class);
    private static final int QUEUE_LIMIT;
    private static final int TOKEN_DELAY_MS;
    
    static {
        String qlStr = System.getenv("QUEUE_LIMIT");
        QUEUE_LIMIT = qlStr != null ? Integer.parseInt(qlStr) : 3;
        String delayStr = System.getenv("TOKEN_DELAY_MS");
        TOKEN_DELAY_MS = delayStr != null ? Integer.parseInt(delayStr) : 50;
    }
    
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private final TokenGenerator tokenGenerator = new TokenGenerator();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
        String json = frame.text();
        Envelope msg = Envelope.fromJson(json);
        
        if (!"START".equals(msg.type)) {
            sendError(ctx, msg.reqId != null ? msg.reqId : "unknown", "BAD_REQUEST", false, "Expected START");
            return;
        }
        
        String reqId = msg.reqId != null ? msg.reqId : "r-" + UUID.randomUUID();
        String sessionId = msg.sessionId != null ? msg.sessionId : "s-" + UUID.randomUUID();
        String workerId = "w-1";
        
        // [OBS] Log routing decision
        kvlog("route", "req=" + reqId, "session=" + sessionId, "worker=" + workerId, "inflight=1", "overloaded=false");
        
        Session session = new Session(sessionId, workerId, QUEUE_LIMIT, ctx.channel());
        sessions.put(sessionId, session);
        
        // Start token streaming in separate thread
        ctx.executor().execute(() -> streamTokens(ctx, session, reqId, msg.prompt));
    }

    private void streamTokens(ChannelHandlerContext ctx, Session session, String reqId, String prompt) {
        List<String> tokens = tokenGenerator.generate(prompt);
        int seq = 0;
        
        for (String token : tokens) {
            if (session.isTerminated()) {
                return;
            }
            
            seq++;
            Envelope tokenEvt = Envelope.token(reqId, seq, token);
            
            if (!session.isWritable()) {
                // [BACKPRESSURE] Try to enqueue
                if (!session.tryEnqueue(tokenEvt)) {
                    // Queue full - send OVERLOADED error
                    kvlog("error", "req=" + reqId, "code=OVERLOADED", "qlen=" + session.queueSize(), "reason=queue_limit");
                    sendError(ctx, reqId, "OVERLOADED", false, "queue_limit_exceeded");
                    session.terminate();
                    return;
                }
                kvlog("backpressure", "req=" + reqId, "seq=" + seq, "qlen=" + session.queueSize(), "action=enqueue");
            } else {
                flushToken(ctx, session, tokenEvt);
            }
            
            // Simulate token generation delay
            try {
                Thread.sleep(TOKEN_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        
        // Send DONE
        if (!session.isTerminated()) {
            Envelope doneEvt = Envelope.done(reqId, seq + 1);
            flushToken(ctx, session, doneEvt);
            kvlog("done", "req=" + reqId, "seq=" + (seq + 1), "total_tokens=" + seq);
            session.terminate();
        }
    }

    private void flushToken(ChannelHandlerContext ctx, Session session, Envelope evt) {
        ctx.writeAndFlush(new TextWebSocketFrame(evt.toJson()));
        kvlog("token", "session=" + session.sessionId, "req=" + evt.reqId, 
              "seq=" + evt.seq, "worker=" + session.workerId, 
              "qlen=" + session.queueSize(), "writable=" + session.isWritable(), 
              "type=" + evt.type);
    }

    private void sendError(ChannelHandlerContext ctx, String reqId, String code, boolean retryable, String message) {
        Envelope error = Envelope.error(reqId, code, retryable, message);
        ctx.writeAndFlush(new TextWebSocketFrame(error.toJson()));
        kvlog("error", "req=" + reqId, "code=" + code, "retryable=" + retryable, "reason=" + message, "close=true");
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) {
        // [BACKPRESSURE] Flush queued messages when channel becomes writable
        sessions.values().stream()
            .filter(s -> s.channel == ctx.channel())
            .forEach(session -> {
                session.setWritable(ctx.channel().isWritable());
                if (session.isWritable()) {
                    Envelope queued;
                    while ((queued = session.poll()) != null) {
                        flushToken(ctx, session, queued);
                    }
                }
            });
        ctx.fireChannelWritabilityChanged();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        kvlog("exception", "error=" + cause.getMessage());
        ctx.close();
    }

    // [OBS] Structured key=value logging
    private void kvlog(String event, String... fields) {
        StringBuilder sb = new StringBuilder();
        sb.append("ts=").append(Instant.now()).append(" event=").append(event);
        for (String field : fields) {
            sb.append(" ").append(field);
        }
        log.info(sb.toString());
    }
}
