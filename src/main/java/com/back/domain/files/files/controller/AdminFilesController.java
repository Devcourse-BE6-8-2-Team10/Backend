package com.back.domain.files.files.controller;

import com.back.domain.files.files.dto.FileUploadResponseDto;
import com.back.domain.files.files.service.FilesService;
import com.back.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/files")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")   // 관리자 권한
public class AdminFilesController {

    private final FilesService filesService;

    // 관리자용 파일 조회 API
    @GetMapping
    public RsData<List<FileUploadResponseDto>> getAllFiles() {
        return filesService.adminGetAllFiles();
    }
}
