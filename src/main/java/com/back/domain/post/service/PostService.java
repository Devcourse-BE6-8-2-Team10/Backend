package com.back.domain.post.service;

import com.back.domain.post.dto.PostDetailDTO;
import com.back.domain.post.dto.PostListDTO;
import com.back.domain.post.dto.PostRequestDTO;
import com.back.domain.post.entity.Post;
import com.back.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    //게시글 생성
    @Transactional
    public PostDetailDTO createPost(PostRequestDTO dto) {
        // 카테고리 변환 예외 처리
        Post.Category category = Post.Category.from(dto.category())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 카테고리입니다."));

        Post post = Post.builder()
                .title(dto.title())
                .description(dto.description())
                .category(category)
                .price(dto.price())
                .status(Post.Status.SALE)
                .build();

        Post saved = postRepository.save(post);
        return new PostDetailDTO(saved, false);
    }

    //게시글 목록 조회
    @Transactional(readOnly = true)
    public List<PostListDTO> getPostList() {
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(PostListDTO::new)
                .toList();
    }

    //인기 게시글 조회
    @Transactional(readOnly = true)
    public List<PostListDTO> getTop10PopularPosts() {
        return postRepository.findTop10ByOrderByFavoriteCntDesc()
                .stream()
                .map(PostListDTO::new)
                .toList();
    }
}
