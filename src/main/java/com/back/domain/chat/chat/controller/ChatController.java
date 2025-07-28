package com.back.domain.chat.chat.controller;

import com.back.domain.chat.chat.dto.MessageDto;
import com.back.domain.chat.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;


    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    public MessageDto sendMessage(MessageDto chatMessage) {
        // 디버깅 로그 추가
        System.out.println("=== 받은 메시지 디버깅 ===");
        System.out.println("chatMessage.getSender(): " + chatMessage.getSender());
        System.out.println("chatMessage.getSenderName(): " + chatMessage.getSenderName());
        System.out.println("chatMessage.getContent(): " + chatMessage.getContent());
        System.out.println("chatMessage.getSenderId(): " + chatMessage.getSenderId());
        System.out.println("chatMessage.getChatRoomId(): " + chatMessage.getChatRoomId());
        System.out.println("========================");
        
        chatService.saveMessage(chatMessage);
        
        // 응답 전 디버깅
        System.out.println("=== 응답할 메시지 디버깅 ===");
        System.out.println("return chatMessage.getSender(): " + chatMessage.getSender());
        System.out.println("return chatMessage.getSenderName(): " + chatMessage.getSenderName());
        System.out.println("return chatMessage.getContent(): " + chatMessage.getContent());
        System.out.println("==========================");
        
        return chatMessage;
    }
}
