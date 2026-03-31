package csd.protocol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.Instant;

/**
 * Protocol message envelope for START/TOKEN/DONE/ERROR events.
 * [PROTO] JSON over WebSocket text frame.
 */
public class Envelope {
    private static final Gson GSON = new GsonBuilder().create();

    public final String type;
    public final String reqId;
    public final String sessionId;
    public final int seq;
    public final String token;
    public final String prompt;
    public final boolean done;
    public final String code;
    public final boolean retryable;
    public final String message;
    public final long ts;

    private Envelope(Builder b) {
        this.type = b.type;
        this.reqId = b.reqId;
        this.sessionId = b.sessionId;
        this.seq = b.seq;
        this.token = b.token;
        this.prompt = b.prompt;
        this.done = b.done;
        this.code = b.code;
        this.retryable = b.retryable;
        this.message = b.message;
        this.ts = Instant.now().toEpochMilli();
    }

    // Factory methods
    public static Envelope start(String reqId, String sessionId, String prompt) {
        return new Builder().type("START").reqId(reqId).sessionId(sessionId).prompt(prompt).build();
    }

    public static Envelope token(String reqId, int seq, String token) {
        return new Builder().type("TOKEN").reqId(reqId).seq(seq).token(token).done(false).build();
    }

    public static Envelope done(String reqId, int seq) {
        return new Builder().type("DONE").reqId(reqId).seq(seq).done(true).build();
    }

    public static Envelope error(String reqId, String code, boolean retryable, String message) {
        return new Builder().type("ERROR").reqId(reqId).code(code).retryable(retryable).message(message).build();
    }

    public String toJson() {
        return GSON.toJson(this);
    }

    public static Envelope fromJson(String json) {
        return GSON.fromJson(json, Envelope.class);
    }

    public static class Builder {
        private String type;
        private String reqId;
        private String sessionId;
        private int seq;
        private String token;
        private String prompt;
        private boolean done;
        private String code;
        private boolean retryable;
        private String message;

        public Builder type(String type) { this.type = type; return this; }
        public Builder reqId(String reqId) { this.reqId = reqId; return this; }
        public Builder sessionId(String sessionId) { this.sessionId = sessionId; return this; }
        public Builder seq(int seq) { this.seq = seq; return this; }
        public Builder token(String token) { this.token = token; return this; }
        public Builder prompt(String prompt) { this.prompt = prompt; return this; }
        public Builder done(boolean done) { this.done = done; return this; }
        public Builder code(String code) { this.code = code; return this; }
        public Builder retryable(boolean retryable) { this.retryable = retryable; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Envelope build() { return new Envelope(this); }
    }
}
