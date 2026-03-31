package csd.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Mock Runtime: deterministic token stream for reproducible demos.
 * [INTENT] Simulates LLM token generation.
 */
public class TokenGenerator {
    
    private final String[] defaultTokens = {"你", "好", "，", "这", "是", "Agent", "Gateway", "!"};
    private final int tokenCount;
    
    public TokenGenerator() {
        // Read token count from env, default 8
        String countStr = System.getenv("TOKEN_COUNT");
        this.tokenCount = countStr != null ? Integer.parseInt(countStr) : 8;
    }
    
    public List<String> generate(String prompt) {
        if (tokenCount <= 8) {
            return Arrays.asList(defaultTokens);
        }
        // Generate more tokens for backpressure testing
        List<String> tokens = new ArrayList<>();
        for (int i = 0; i < tokenCount; i++) {
            tokens.add("T" + (i + 1));
        }
        return tokens;
    }
    
    public List<String> generateSlow(String prompt, int count) {
        // Generate specified number of tokens
        String[] tokens = new String[count];
        for (int i = 0; i < count; i++) {
            tokens[i] = "T" + (i + 1);
        }
        return Arrays.asList(tokens);
    }
}
