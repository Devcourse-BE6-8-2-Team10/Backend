package com.back.domain.files.files.controller;

import com.back.domain.files.files.dto.FileUploadResponseDto;
import com.back.domain.files.files.service.FilesService;
import com.back.global.rsData.RsData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminFileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FilesService filesService;

    @Test
    @DisplayName("관리자 전체 파일 목록 조회 - 성공")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllFiles_success() throws Exception {
        // given
        List<FileUploadResponseDto> mockFileList = List.of(
                new FileUploadResponseDto(1L, 10L, "test.png", "image/png", 2048L, "http://example.com/test.png", 1, LocalDateTime.now()),
                new FileUploadResponseDto(2L, 10L, "doc.pdf", "application/pdf", 4096L, "http://example.com/doc.pdf", 2, LocalDateTime.now())
        );
        RsData<List<FileUploadResponseDto>> response = new RsData<>("200", "파일 목록 조회 성공", mockFileList);

        Mockito.when(filesService.adminGetAllFiles()).thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/admin/files"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200"))
                .andExpect(jsonPath("$.msg").value("파일 목록 조회 성공"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].fileName").value("test.png"))
                .andExpect(jsonPath("$.data[1].fileName").value("doc.pdf"));
    }

    @Test
    @DisplayName("관리자 전체 파일 목록 조회 - 파일 없음")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllFiles_whenEmpty() throws Exception {
        // given
        RsData<List<FileUploadResponseDto>> emptyResponse = new RsData<>(
                "200",
                "등록된 파일이 없습니다.",
                List.of()
        );

        Mockito.when(filesService.adminGetAllFiles()).thenReturn(emptyResponse);

        // when & then
        mockMvc.perform(get("/api/admin/files"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200"))
                .andExpect(jsonPath("$.msg").value("등록된 파일이 없습니다."))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }
}