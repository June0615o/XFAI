package com.junevi.lc4jtrue.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import reactor.core.publisher.Flux;

/*@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT, //手动装配(实际上默认)
        streamingChatModel = "openAiStreamingChatModel",
        //chatMemory = "chatMemory" //配置好的会话记忆
        chatMemoryProvider = "chatMemoryProvider",
        contentRetriever = "contentRetriever",
        tools = "reservationTool",
        toolProvider = "mcpToolProvider"

)*/
public interface ConsultantService {
    @SystemMessage(fromResource = "system.txt")
    public Flux<String> chat(
            @MemoryId String memoryId,
            @UserMessage String message);
}
