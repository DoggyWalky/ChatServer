package com.chatServer.config;

import com.chatServer.subscriber.ChatMessageSubscriber;
import com.chatServer.subscriber.ChatRoomSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {


    /**
     * 채팅 내용을 전달하기 위한 채널
     */
    @Bean
    public ChannelTopic chatMessageTopic() {
        return new ChannelTopic("chatMessage");
    }

    /**
     * 채팅방에 대한 명령을 전달하기 위한 채널
     */
    @Bean
    public ChannelTopic chatRoomTopic() {
        return new ChannelTopic("chatRoom");
    }

    /**
     * redis에 발행(publish)된 메시지 처리를 위한 리스너 설정
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListener(RedisConnectionFactory connectionFactory,
                                                              MessageListenerAdapter chatRoomListenerAdapter,
                                                              MessageListenerAdapter chatMessageListenerAdapter,
                                                              ChannelTopic chatMessageTopic,
                                                              ChannelTopic chatRoomTopic) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(chatMessageListenerAdapter, chatMessageTopic);
        container.addMessageListener(chatRoomListenerAdapter, chatRoomTopic);
        return container;
    }

    /**
     * Redis에 발행되는 실제 메시지(채팅방 생성)를 처리하는 subscriber 설정 추가
     */
    @Bean
    public MessageListenerAdapter chatRoomListenerAdapter(ChatRoomSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "sendMessage");
    }

    /**
     * Redis에 발행되는 실제 메시지(채팅 메시지)를 처리하는 subscriber 설정 추가
     */
    @Bean
    public MessageListenerAdapter chatMessageListenerAdapter(ChatMessageSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "sendMessage");
    }

    /**
     * 어플리케이션에서 사용할 redisTemplate 설정
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
        return redisTemplate;
    }
}
