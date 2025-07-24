package com.back.domain.chat.chat.entity;

import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class RoomParticipant extends BaseEntity {
    // room , user 메니투원 설정
    @ManyToOne
    private ChatRoom chatRoom;

    private LocalDateTime leftAt;

    private boolean isActive;
}
