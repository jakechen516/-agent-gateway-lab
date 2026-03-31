package csd.mock;

import csd.agent.AgentRuntime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Mock Runtime: deterministic token stream for reproducible demos.
 * [INTENT] Simulates LLM token generation.
 */
public class TokenGenerator implements AgentRuntime {
    
    private final String[] defaultTokens = {"你", "好", "，", "这", "是", "Agent", "Gateway", "!"};
    private final int tokenCount;
    private final int tokenDelayMs;
    
    public TokenGenerator() {
        // Read config from env
        String countStr = System.getenv("TOKEN_COUNT");
        this.tokenCount = countStr != null ? Integer.parseInt(countStr) : 8;
        String delayStr = System.getenv("TOKEN_DELAY_MS");
        this.tokenDelayMs = delayStr != null ? Integer.parseInt(delayStr) : 50;
    }
    
    @Override
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
    
    @Override
    public void streamGenerate(String prompt, Consumer<String> tokenConsumer, Runnable onComplete) {
        List<String> tokens = generate(prompt);
        for (String token : tokens) {
            tokenConsumer.accept(token);
            try {
                Thread.sleep(tokenDelayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        onComplete.run();
    }
    
    @Override
    public boolean isAvailable() {
        return true; // Mock is always available
    }
    
    @Override
    public String getName() {
        return "MockTokenGenerator";
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
