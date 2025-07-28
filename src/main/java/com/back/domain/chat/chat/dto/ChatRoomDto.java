package com.back.domain.chat.chat.dto;

public record ChatRoomDto (
    Long id,
    String name,
    Long postId,
    String lastContent
) {
    public ChatRoomDto(Long id, String name, Long postId, String lastContent) {
        this.id = id;
        this.name = name;
        this.postId = postId;
        this.lastContent = lastContent;
    }

    public static ChatRoomDto from(Long id, String name, Long postId, String lastContent) {
        return new ChatRoomDto(id, name, postId, lastContent);
    }
}
