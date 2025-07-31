package com.back.domain.post.dto;

public record FavoriteResponseDTO(
        boolean isLiked,
        int favoriteCnt,
        String message
) {}