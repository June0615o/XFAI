package com.junevi.lc4jtrue.repository;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;

@Repository
public class RedisChatMemoryStore implements ChatMemoryStore {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        //根据memoryId从redis中获取消息
        String redisKey = "XFAI:memoryId:"+memoryId.toString();
        String json = stringRedisTemplate.opsForValue().get(redisKey);
        //需要将json字符串转换成List存入result中返回
        List<ChatMessage> result = ChatMessageDeserializer.messagesFromJson(json);
        return result;
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> list) {
        //先将List转换成json数据，才能考虑存储到redis中
        String json = ChatMessageSerializer.messagesToJson(list);
        String redisKey = "XFAI:memoryId:"+memoryId.toString();
        //键名为: XFAI:memoryId:{memoryId},指定ttl为24h.
        stringRedisTemplate.opsForValue().set(redisKey,json, Duration.ofDays(1));

    }

    @Override
    public void deleteMessages(Object memoryId) {
        String redisKey = "XFAI:memoryId:"+memoryId.toString();
        stringRedisTemplate.delete(redisKey);
    }
}
