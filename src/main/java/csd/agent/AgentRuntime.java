package csd.agent;

import java.util.List;
import java.util.function.Consumer;

/**
 * AI Agent Runtime interface.
 * [INTENT] Abstraction for different AI backends (Mock, Ollama, OpenAI).
 */
public interface AgentRuntime {
    
    /**
     * Generate tokens for a given prompt.
     * @param prompt User input
     * @return List of tokens
     */
    List<String> generate(String prompt);
    
    /**
     * Stream tokens with callback.
     * @param prompt User input  
     * @param tokenConsumer Called for each token
     * @param onComplete Called when stream ends
     */
    void streamGenerate(String prompt, Consumer<String> tokenConsumer, Runnable onComplete);
    
    /**
     * Check if the runtime is available.
     */
    boolean isAvailable();
    
    /**
     * Get runtime name for logging.
     */
    String getName();
}
