package com.back.domain.post.controller;

import com.back.domain.post.dto.PostRequestDTO;
import com.back.domain.post.dto.PostDetailDTO;
import com.back.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostDetailDTO> createPost(@RequestBody PostRequestDTO dto) {
        PostDetailDTO result = postService.createPost(dto);
        return ResponseEntity.ok(result);
    }
}
