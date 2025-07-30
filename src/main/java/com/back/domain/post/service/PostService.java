package com.back.domain.post.service;

import com.back.domain.member.entity.Member;
import com.back.domain.post.dto.PostDetailDTO;
import com.back.domain.post.dto.PostListDTO;
import com.back.domain.post.dto.PostRequestDTO;
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

    //게시글 생성
    @Transactional
    public PostDetailDTO createPost(PostRequestDTO dto) {
        Member member = getCurrentMemberOrThrow();

        // 카테고리 변환 예외 처리
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

    // 게시글 상세 조회
    @Transactional(readOnly = true)
    public RsData<PostDetailDTO> getPostDetail(Long postId) {
        Member member = getCurrentMemberOrThrow();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ServiceException("NOT_FOUND", "게시글이 존재하지 않습니다"));

        boolean isLiked = favoritePostRepository.existsByMemberAndPost(member, post);
        return new RsData<>("SUCCESS", "게시글 조회 성공", new PostDetailDTO(post, isLiked));
    }

    //인기 게시글 조회
    @Transactional(readOnly = true)
    public List<PostListDTO> getTop10PopularPosts() {
        return postRepository.findTop10ByOrderByFavoriteCntDesc()
                .stream()
                .map(PostListDTO::new)
                .toList();
    }

    // 찜 등록
    @Transactional
    public RsData<String> addFavorite(Long postId) {
        Member member = getCurrentMemberOrThrow();
        Post post = getPostOrThrow(postId);
        validateAlreadyLiked(member, post, true);
        FavoritePost favorite = FavoritePost.builder()
                .member(member)
                .post(post)
                .build();
        favoritePostRepository.save(favorite);
        post.increaseFavoriteCnt();
        return new RsData<>("SUCCESS", String.format("'%s' 찜 등록 성공", post.getTitle()));
    }

    // 찜 해제
    @Transactional
    public RsData<String> removeFavorite(Long postId) {
        Member member = getCurrentMemberOrThrow();
        Post post = getPostOrThrow(postId);
        validateAlreadyLiked(member, post, false);
        favoritePostRepository.deleteByMemberAndPost(member, post);
        post.decreaseFavoriteCnt();
        return new RsData<>("SUCCESS", String.format("'%s' 찜 해제 성공", post.getTitle()));
    }

    //------------------------------------------------------------------

    //현재 로그인 유저 확인
    private Member getCurrentMemberOrThrow() {
        Member member = rq.getMember();
        if (member == null) {
            throw new ServiceException("UNAUTHORIZED", "로그인이 필요합니다.");
        }
        return member;
    }

    // 찜 중복 여부 체크
    private void validateAlreadyLiked(Member member, Post post, boolean forAdd) {
        boolean exists = favoritePostRepository.existsByMemberAndPost(member, post);
        if (forAdd && exists) {
            throw new ServiceException("BAD_REQUEST", "이미 찜한 게시글입니다.");
        }
        if (!forAdd && !exists) {
            throw new ServiceException("BAD_REQUEST", "찜하지 않은 게시글입니다.");
        }
    }

    // 게시글 조회 에러
    private Post getPostOrThrow(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ServiceException("NOT_FOUND", "게시글이 존재하지 않습니다."));
    }
}
