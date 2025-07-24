package com.back.domain.trade.dto;


import com.back.domain.trade.entity.Trade;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record TradeDto(
    Long id,
    Long postId,
    Long sellerId,
    Long buyerId,
    int price,
    String status,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt
) {
public TradeDto(Trade trade) {
        this(
            trade.getId(),
            trade.getPost().getId(),
            trade.getSeller().getId(),
            trade.getBuyer().getId(),
            trade.getPrice(),
            trade.getStatus().name(),
            trade.getCreatedAt()
        );
    }
}