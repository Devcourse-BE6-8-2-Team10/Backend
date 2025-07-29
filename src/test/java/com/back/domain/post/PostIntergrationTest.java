package com.back.domain.post;

import com.back.domain.post.dto.PostRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PostIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("게시글 생성 테스트")
    void createPostTest() throws Exception {
        PostRequestDTO requestDTO = new PostRequestDTO(
                "테스트 게시글 제목",
                "이건 통합 테스트용 설명입니다.",
                "PRODUCT",  // 문자열로 전달
                9999
        );

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("게시글 목록 조회 테스트")
    void getPostListTest() throws Exception {
        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("인기차트 조회 테스트")
    void getTop10PopularPostsTest() throws Exception {
        mockMvc.perform(get("/api/posts/popular"))
                .andExpect(status().isOk());
    }
}
