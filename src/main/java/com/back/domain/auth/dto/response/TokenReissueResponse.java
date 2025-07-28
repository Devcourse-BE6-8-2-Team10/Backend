package com.back.domain.auth.dto.response;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public record TokenReissueResponse(String accessToken, String refreshToken){
}
