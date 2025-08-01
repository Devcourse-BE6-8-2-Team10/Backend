package com.back.domain.post.controller;

import com.back.domain.post.dto.PostDetailDTO;
import com.back.domain.post.dto.PostListDTO;
import com.back.domain.post.dto.PostRequestDTO;
import com.back.domain.post.service.PostService;
import com.back.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @Operation(summary = "게시글 등록")
    @PostMapping
    public RsData<PostDetailDTO> createPost(@Valid @RequestBody PostRequestDTO dto) {
        return postService.createPost(dto);
    }

    @Operation(summary = "게시글 목록 조회")
    @GetMapping
    public RsData<List<PostListDTO>> getPostList() {
        return postService.getPostList();
    }

    @Operation(summary = "게시글 상세 조회")
    @GetMapping("/{postId}")
    public RsData<PostDetailDTO> getPostDetail(@PathVariable Long postId) {
        return postService.getPostDetail(postId);
    }

    @Operation(summary = "인기 게시글 조회")
    @GetMapping("/popular")
    public RsData<List<PostListDTO>> getTop10PopularPosts() {
        return postService.getTop10PopularPosts();
    }
}
