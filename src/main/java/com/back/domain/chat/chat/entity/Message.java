package com.back.domain.chat.chat.entity;

import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Message extends BaseEntity {
    //member, chatroom 관계 설정
    @ManyToOne
    private ChatRoom chatRoom;

    private String senderName;

    private String content;

}
