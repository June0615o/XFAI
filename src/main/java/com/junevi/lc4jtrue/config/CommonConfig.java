package com.junevi.lc4jtrue.config;

import com.junevi.lc4jtrue.repository.RedisChatMemoryStore;
import com.junevi.lc4jtrue.service.ConsultantService;
import com.junevi.lc4jtrue.tools.ReservationTool;
import dev.langchain4j.community.store.embedding.redis.RedisEmbeddingStore;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import dev.langchain4j.data.document.Document;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class CommonConfig {

    @Autowired
    private OpenAiStreamingChatModel model;
    @Autowired
    private RedisChatMemoryStore redisChatMemoryStore;
    @Autowired
    private EmbeddingModel embeddingModel;
    @Autowired
    private RedisEmbeddingStore redisEmbeddingStore;
    @Bean
    public ChatMemory chatMemory() {
        MessageWindowChatMemory memory = MessageWindowChatMemory.builder()
                .maxMessages(20)
                .build();
        return memory;
    }
    @Bean
    public ChatMemoryProvider chatMemoryProvider() {
        ChatMemoryProvider chatMemoryProvider = new ChatMemoryProvider() {
            @Override
            public ChatMemory get(Object memoryId) {
                return MessageWindowChatMemory.builder()
                        .id(memoryId)
                        .maxMessages(20)
                        .chatMemoryStore(redisChatMemoryStore)
                        .build();
            }
        };
        return chatMemoryProvider;
    }

    //构建文档分割器
    DocumentSplitter ds = DocumentSplitters.recursive(1000,200);
    //构建向量数据库操作对象 EmbeddingStore (原先操作的是内存版本的向量数据库)
    //@Bean(name = "customEmbeddingStore")
    /*@Primary
      在对固定的content内容切割处理后的向量持久化到Redis-Stack后
      我们已经不再需要每次都执行embeddingStore构造方法从而每次都切割、存储了
     */
    public EmbeddingStore embeddingStore() {
        List<Document> documents = ClassPathDocumentLoader.loadDocuments("content");

        // 拆分所有文档为 TextSegment
        List<TextSegment> allSegments = new ArrayList<>();
        for (Document doc : documents) {
            List<TextSegment> segments = ds.split(doc); // ds 是你的 DocumentSplitter
            allSegments.addAll(segments);
        }

        // 分批嵌入并存入 store
        int batchSize = 10;
        for (int i = 0; i < allSegments.size(); i += batchSize) {
            int end = Math.min(i + batchSize, allSegments.size());
            List<TextSegment> batch = allSegments.subList(i, end);
            List<Embedding> embeddings = embeddingModel.embedAll(batch).content();
            redisEmbeddingStore.addAll(embeddings, batch);
        }

        return redisEmbeddingStore;
    }
    //向量数据库检索对象EmbeddingStoreContentRetriever
    @Bean
    public ContentRetriever contentRetriever(){
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(redisEmbeddingStore)
                .minScore(0.5)
                .maxResults(3)
                .embeddingModel(embeddingModel)
                .build();
    }
    @Bean
    public ConsultantService consultantService(ContentRetriever contentRetriever,
                                               ChatMemoryProvider chatMemoryProvider,
                                               ReservationTool reservationTool,
                                               McpToolProvider mcpToolProvider) {
        return AiServices.builder(ConsultantService.class)
                .streamingChatModel(model)
                .chatMemoryProvider(chatMemoryProvider)
                .contentRetriever(contentRetriever)
                .tools(reservationTool)         // 本地 Tool
                .toolProvider(mcpToolProvider)  // MCP ToolProvider
                .build();
    }

}
