package com.junevi.lc4jtrue.controller;

import com.junevi.lc4jtrue.service.ConsultantService;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {
    @Autowired
    private ConsultantService consultantService;
    @RequestMapping(value="/chat",produces = "text/html;charset=utf-8")
    public Flux<String> chat(String memoryId,String message){
        Flux<String> result = consultantService.chat(memoryId,message);
        return result;
    }
    /*@Autowired
    OpenAiChatModel model;

    @RequestMapping("/chat")
    public String chat(String message) {
        String result = model.chat(message);
        return result;
    }*/

}
