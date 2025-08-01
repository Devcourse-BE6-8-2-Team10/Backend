package com.back.domain.post.service;

import com.back.domain.member.entity.Member;
import com.back.domain.post.dto.*;
import com.back.domain.post.entity.FavoritePost;
import com.back.domain.post.entity.Post;
import com.back.domain.post.repository.FavoritePostRepository;
import com.back.domain.post.repository.PostRepository;
import com.back.global.exception.ServiceException;
import com.back.global.rq.Rq;
import com.back.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final FavoritePostRepository favoritePostRepository;
    private final Rq rq;

    // 게시글 등록
    @Transactional
    public RsData<PostDetailDTO> createPost(PostRequestDTO dto) {
        Member member = getCurrentMemberOrThrow();

        Post.Category category = Post.Category.from(dto.category())
                .orElseThrow(() -> new ServiceException("BAD_REQUEST", "유효하지 않은 카테고리입니다."));

        Post post = Post.builder()
                .title(dto.title())
                .description(dto.description())
                .category(category)
                .price(dto.price())
                .member(member)
                .status(Post.Status.SALE)
                .build();

        Post saved = postRepository.save(post);
        return new RsData<>("SUCCESS", "게시글 등록 완료", new PostDetailDTO(saved, false));
    }

    // 게시글 목록 조회
    @Transactional(readOnly = true)
    public RsData<List<PostListDTO>> getPostList() {
        List<PostListDTO> result = postRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(PostListDTO::new).toList();
        return new RsData<>("SUCCESS", "게시글 목록 조회 성공", result);
    }

    // 게시글 상세 조회
    @Transactional(readOnly = true)
    public RsData<PostDetailDTO> getPostDetail(Long postId) {
        Member member = getCurrentMemberOrThrow();
        Post post = getPostOrThrow(postId);

        boolean isLiked = favoritePostRepository.existsByMemberAndPost(member, post);
        return new RsData<>("SUCCESS", "게시글 조회 성공", new PostDetailDTO(post, isLiked));
    }

    // 인기 게시글 조회
    @Transactional(readOnly = true)
    public RsData<List<PostListDTO>> getTop10PopularPosts() {
        List<PostListDTO> result = postRepository.findTop10ByOrderByFavoriteCntDesc()
                .stream().map(PostListDTO::new).toList();
        return new RsData<>("SUCCESS", "인기 게시글 조회 성공", result);
    }

    // 찜 등록/해제
    @Transactional
    public RsData<FavoriteResponseDTO> toggleFavorite(Long postId) {
        Member member = getCurrentMemberOrThrow();
        Post post = getPostForUpdateOrThrow(postId);

        if (post.getMember().equals(member)) {
            return new RsData<>("FAIL", "자신의 게시글은 찜할 수 없습니다.", null);
        }

        boolean alreadyLiked = favoritePostRepository.existsByMemberAndPost(member, post);

        if (alreadyLiked) {
            // 찜 해제
            favoritePostRepository.deleteByMemberAndPost(member, post);
            postRepository.decreaseFavoriteCnt(postId);

            int newFavoriteCnt = postRepository.findById(postId)
                    .orElseThrow(() -> new ServiceException("NOT_FOUND", "게시글이 존재하지 않습니다."))
                    .getFavoriteCnt();

            return new RsData<>("SUCCESS", "찜 해제 완료",
                    new FavoriteResponseDTO(post.getId(), false, newFavoriteCnt,
                            String.format("'%s' 찜 해제 완료", post.getTitle()))
            );
        } else {
            // 찜 등록
            try {
                favoritePostRepository.save(FavoritePost.builder()
                        .member(member)
                        .post(post)
                        .build());

                postRepository.increaseFavoriteCnt(postId);

                int newFavoriteCnt = postRepository.findById(postId)
                        .orElseThrow(() -> new ServiceException("NOT_FOUND", "게시글이 존재하지 않습니다."))
                        .getFavoriteCnt();

                return new RsData<>("SUCCESS", "찜 등록 완료",
                        new FavoriteResponseDTO(post.getId(), true, newFavoriteCnt,
                                String.format("'%s' 찜 등록 완료", post.getTitle()))
                );
            } catch (Exception e) {
                throw new ServiceException("CONFLICT", "이미 찜한 게시글입니다.");
            }
        }
    }

    //------------------------------------------------------------------

    private Member getCurrentMemberOrThrow() {
        Member member = rq.getMember();
        if (member == null) {
            throw new ServiceException("UNAUTHORIZED", "로그인이 필요합니다.");
        }
        return member;
    }

    private Post getPostOrThrow(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ServiceException("NOT_FOUND", "게시글이 존재하지 않습니다."));
    }

    private Post getPostForUpdateOrThrow(Long postId) {
        return postRepository.findByIdForUpdate(postId)
                .orElseThrow(() -> new ServiceException("NOT_FOUND", "오류 입니다."));
    }
}
