package com.back.domain.chat.redis.listener;

import com.back.domain.chat.chat.dto.MessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;
    
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            log.info("=== Redis에서 메시지 수신 ===");
            
            // Redis에서 받은 메시지를 MessageDto로 변환
            String messageBody = new String(message.getBody());
            log.info("수신된 메시지: {}", messageBody);
            
            MessageDto chatMessage = objectMapper.readValue(messageBody, MessageDto.class);
            
            log.info("변환된 메시지 - 채팅방: {}, 발신자: {}, 내용: {}", 
                    chatMessage.getChatRoomId(), 
                    chatMessage.getSender(), 
                    chatMessage.getContent());
            
            // WebSocket을 통해 클라이언트들에게 전송
            String destination = "/queue/chat/" + chatMessage.getChatRoomId();
            
            // 채팅방 참여자들에게 브로드캐스트
            messagingTemplate.convertAndSend("/topic/chat/" + chatMessage.getChatRoomId(), chatMessage);
            
            log.info("WebSocket으로 메시지 전송 완료: {}", destination);
            
        } catch (Exception e) {
            log.error("Redis 메시지 처리 중 에러 발생: {}", e.getMessage(), e);
        }
    }
}
