package com.back.domain.chat.chat.service;

import com.back.domain.chat.chat.dto.MessageDto;
import com.back.domain.chat.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository messageRepository;

    public void saveMessage(MessageDto chatMessage) {
//        Member sender = messageRepository.findById(chatMessage.getSenderId())
//                .orElseThrow(() -> new ServiceException("404-1","사용자를 찾을수 없습니다.")).getSender();
//
//        Message message = new Message(chatMessage, sender);
    }
}
