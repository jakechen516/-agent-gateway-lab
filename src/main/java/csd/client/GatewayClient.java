package csd.client;

import csd.protocol.Envelope;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;
import java.time.Instant;
import java.util.UUID;

/**
 * WebSocket client for testing Agent Gateway.
 * [INTENT] Sends START and receives TOKEN/DONE/ERROR.
 */
public class GatewayClient {

    private static final Logger log = LoggerFactory.getLogger(GatewayClient.class);
    private final String host;
    private final int port;
    private final int consumeDelayMs;
    private Channel channel;
    private EventLoopGroup group;

    public GatewayClient(String host, int port, int consumeDelayMs) {
        this.host = host;
        this.port = port;
        this.consumeDelayMs = consumeDelayMs;
    }

    public void connect() throws Exception {
        group = new NioEventLoopGroup();
        URI uri = new URI("ws://" + host + ":" + port + "/ws");

        WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(
            uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders());

        ClientHandler handler = new ClientHandler(handshaker, consumeDelayMs);

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new HttpClientCodec());
                    pipeline.addLast(new HttpObjectAggregator(65536));
                    pipeline.addLast(handler);
                }
            });

        channel = bootstrap.connect(host, port).sync().channel();
        handler.handshakeFuture().sync();
        kvlog("client_connect", "host=" + host, "port=" + port);
    }

    public void sendStart(String sessionId, String prompt) {
        String reqId = "r-" + UUID.randomUUID().toString().substring(0, 8);
        Envelope start = Envelope.start(reqId, sessionId, prompt);
        channel.writeAndFlush(new TextWebSocketFrame(start.toJson()));
        kvlog("client_send", "type=START", "req=" + reqId, "session=" + sessionId);
    }

    public void close() {
        if (channel != null) {
            channel.close();
        }
        if (group != null) {
            group.shutdownGracefully();
        }
    }

    private void kvlog(String event, String... fields) {
        StringBuilder sb = new StringBuilder();
        sb.append("ts=").append(Instant.now()).append(" event=").append(event);
        for (String field : fields) {
            sb.append(" ").append(field);
        }
        log.info(sb.toString());
    }

    // Inner handler class
    private static class ClientHandler extends SimpleChannelInboundHandler<Object> {
        private final WebSocketClientHandshaker handshaker;
        private final int consumeDelayMs;
        private ChannelPromise handshakeFuture;

        public ClientHandler(WebSocketClientHandshaker handshaker, int consumeDelayMs) {
            this.handshaker = handshaker;
            this.consumeDelayMs = consumeDelayMs;
        }

        public ChannelFuture handshakeFuture() {
            return handshakeFuture;
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) {
            handshakeFuture = ctx.newPromise();
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            handshaker.handshake(ctx.channel());
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (!handshaker.isHandshakeComplete()) {
                handshaker.finishHandshake(ctx.channel(), (FullHttpResponse) msg);
                handshakeFuture.setSuccess();
                return;
            }

            if (msg instanceof TextWebSocketFrame) {
                String json = ((TextWebSocketFrame) msg).text();
                Envelope evt = Envelope.fromJson(json);
                
                // Simulate slow consumer
                if (consumeDelayMs > 0) {
                    Thread.sleep(consumeDelayMs);
                }
                
                kvlog("client_recv", "type=" + evt.type, "req=" + evt.reqId, "seq=" + evt.seq);
                
                if ("DONE".equals(evt.type) || "ERROR".equals(evt.type)) {
                    kvlog("client_close", "reason=" + evt.type);
                    ctx.close();
                }
            } else if (msg instanceof CloseWebSocketFrame) {
                ctx.close();
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            kvlog("client_error", "error=" + cause.getMessage());
            if (!handshakeFuture.isDone()) {
                handshakeFuture.setFailure(cause);
            }
            ctx.close();
        }

        private static final Logger handlerLog = LoggerFactory.getLogger(ClientHandler.class);
        
        private void kvlog(String event, String... fields) {
            StringBuilder sb = new StringBuilder();
            sb.append("ts=").append(Instant.now()).append(" event=").append(event);
            for (String field : fields) {
                sb.append(" ").append(field);
            }
            handlerLog.info(sb.toString());
        }
    }

    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "127.0.0.1";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 8765;
        int delay = args.length > 2 ? Integer.parseInt(args[2]) : 0;
        String sessionId = args.length > 3 ? args[3] : "s-01";

        GatewayClient client = new GatewayClient(host, port, delay);
        try {
            client.connect();
            client.sendStart(sessionId, "hello");
            // Wait for completion
            Thread.sleep(5000);
        } finally {
            client.close();
        }
    }
}
