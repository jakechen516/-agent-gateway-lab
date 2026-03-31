package csd.agent;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.output.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.function.Consumer;

/**
 * AI Agent Runtime using LangChain4j + Ollama.
 * [INTENT] Real AI model integration replacing mock TokenGenerator.
 */
public class OllamaRuntime {
    
    private static final Logger log = LoggerFactory.getLogger(OllamaRuntime.class);
    
    private final String baseUrl;
    private final String modelName;
    private final ChatLanguageModel chatModel;
    private final StreamingChatLanguageModel streamingModel;
    
    public OllamaRuntime() {
        this("http://localhost:11434", "qwen2:7b");
    }
    
    public OllamaRuntime(String baseUrl, String modelName) {
        this.baseUrl = baseUrl;
        this.modelName = modelName;
        
        // Non-streaming model for simple requests
        this.chatModel = OllamaChatModel.builder()
            .baseUrl(baseUrl)
            .modelName(modelName)
            .timeout(Duration.ofMinutes(2))
            .build();
        
        // Streaming model for token-by-token output
        this.streamingModel = OllamaStreamingChatModel.builder()
            .baseUrl(baseUrl)
            .modelName(modelName)
            .timeout(Duration.ofMinutes(2))
            .build();
        
        log.info("ts={} event=agent_init baseUrl={} model={}", 
            System.currentTimeMillis(), baseUrl, modelName);
    }
    
    /**
     * Simple chat - returns complete response.
     */
    public String chat(String prompt) {
        log.info("ts={} event=agent_chat_start prompt_len={}", 
            System.currentTimeMillis(), prompt.length());
        
        try {
            String response = chatModel.generate(prompt);
            log.info("ts={} event=agent_chat_done response_len={}", 
                System.currentTimeMillis(), response.length());
            return response;
        } catch (Exception e) {
            log.error("ts={} event=agent_chat_error error={}", 
                System.currentTimeMillis(), e.getMessage());
            throw e;
        }
    }
    
    /**
     * Streaming chat - calls tokenConsumer for each token.
     */
    public void streamChat(String prompt, Consumer<String> tokenConsumer, Runnable onComplete) {
        log.info("ts={} event=agent_stream_start prompt_len={}", 
            System.currentTimeMillis(), prompt.length());
        
        streamingModel.generate(prompt, new dev.langchain4j.model.StreamingResponseHandler<>() {
            @Override
            public void onNext(String token) {
                tokenConsumer.accept(token);
            }
            
            @Override
            public void onComplete(Response response) {
                log.info("ts={} event=agent_stream_done", System.currentTimeMillis());
                onComplete.run();
            }
            
            @Override
            public void onError(Throwable error) {
                log.error("ts={} event=agent_stream_error error={}", 
                    System.currentTimeMillis(), error.getMessage());
            }
        });
    }
    
    /**
     * Check if Ollama service is available.
     */
    public boolean isAvailable() {
        try {
            // Simple health check
            chatModel.generate("hello");
            return true;
        } catch (Exception e) {
            log.warn("ts={} event=agent_unavailable error={}", 
                System.currentTimeMillis(), e.getMessage());
            return false;
        }
    }
    
    public String getModelName() {
        return modelName;
    }
    
    public String getBaseUrl() {
        return baseUrl;
    }
}
