package com.back.domain.chat.chat.controller;

import com.back.domain.chat.chat.dto.MessageDto;
import com.back.domain.chat.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/sendMessage")
    public void sendMessage(MessageDto chatMessage) {  // void로 변경, @SendTo 제거
        System.out.println("=== 받은 메시지 ===");
        System.out.println("sender: " + chatMessage.getSender());
        System.out.println("content: " + chatMessage.getContent());
        System.out.println("senderId: " + chatMessage.getSenderId());
        System.out.println("chatRoomId: " + chatMessage.getChatRoomId());
        System.out.println("=================");

        try {
            chatService.saveMessage(chatMessage);

            // 채팅방 참여자들 조회
            List<String> participants = chatService.getParticipants(chatMessage.getChatRoomId());
            
            System.out.println("=== 참여자 조회 결과 ===");
            System.out.println("채팅방 ID: " + chatMessage.getChatRoomId());
            System.out.println("참여자 수: " + participants.size());
            for (String participant : participants) {
                System.out.println("참여자: " + participant);
            }
            System.out.println("======================");

            // 각 참여자에게 개별 전송
            for (String userEmail : participants) {
                System.out.println("메시지 전송 중 -> 사용자 이메일: " + userEmail + ", 경로: /queue/chat/" + chatMessage.getChatRoomId());
                messagingTemplate.convertAndSendToUser(
                        userEmail,
                        "/queue/chat/" + chatMessage.getChatRoomId(),
                        chatMessage
                );
                System.out.println("메시지 전송 완료 -> " + userEmail);
            }
            
            if (participants.isEmpty()) {
                System.out.println("⚠️ 경고: 참여자가 없어서 메시지가 전송되지 않았습니다!");
            }
        } catch (Exception e) {
            System.out.println("에러 발생: " + e.getMessage());
            e.printStackTrace();

            // 에러 메시지는 발신자에게만 전송
            MessageDto errorMessage = new MessageDto();
            errorMessage.setSender("System");
            errorMessage.setContent("메시지 전송에 실패했습니다: " + e.getMessage());

            messagingTemplate.convertAndSendToUser(
                    chatMessage.getSenderEmail(),
                    "/queue/error",
                    errorMessage
            );
        }
    }
}
