package com.back.domain.post.controller;

import com.back.domain.post.service.PostService;
import com.back.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
public class FavoriteController {

    private final PostService postService;

    @Operation(summary = "찜 등록")
    @PostMapping("/{postId}")
    public RsData<String> addFavorite(@PathVariable Long postId) {
        return postService.addFavorite(postId);
    }

    @Operation(summary = "찜 해제")
    @DeleteMapping("/{postId}")
    public RsData<String> removeFavorite(@PathVariable Long postId) {
        return postService.removeFavorite(postId);
    }

}

