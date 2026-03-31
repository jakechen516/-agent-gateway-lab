package csd.gateway;

import csd.protocol.Envelope;
import io.netty.channel.Channel;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Session state with bounded outbound queue.
 * [BACKPRESSURE] Queue limit prevents OOM.
 */
public class Session {
    public final String sessionId;
    public final String workerId;
    public final int queueLimit;
    public final Queue<Envelope> outbound;
    public final Channel channel;
    private volatile boolean writable;
    private volatile boolean terminated;

    public Session(String sessionId, String workerId, int queueLimit, Channel channel) {
        this.sessionId = sessionId;
        this.workerId = workerId;
        this.queueLimit = queueLimit;
        this.outbound = new ArrayDeque<>();
        this.channel = channel;
        this.writable = true;
        this.terminated = false;
    }

    public boolean isWritable() {
        return writable && channel.isWritable();
    }

    public void setWritable(boolean writable) {
        this.writable = writable;
    }

    public boolean isTerminated() {
        return terminated;
    }

    public void terminate() {
        this.terminated = true;
        this.writable = false;
    }

    public boolean tryEnqueue(Envelope evt) {
        if (outbound.size() >= queueLimit) {
            return false;
        }
        outbound.add(evt);
        return true;
    }

    public Envelope poll() {
        return outbound.poll();
    }

    public int queueSize() {
        return outbound.size();
    }
}
