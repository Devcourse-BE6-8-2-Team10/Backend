package com.back.domain.files.files.service;

import com.back.domain.files.files.dto.FileUploadResponseDto;
import com.back.domain.files.files.entity.Files;
import com.back.domain.files.files.repository.FilesRepository;
import com.back.domain.post.entity.Post;
import com.back.domain.post.repository.PostRepository;
import com.back.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FilesService {

    private final FilesRepository filesRepository;
    private final FileStorageService fileStorageService;
    private final PostRepository postRepository;

    // 파일 업로드 서비스
    public RsData<List<FileUploadResponseDto>> uploadFiles(Long postId, MultipartFile[] files) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다: " + postId));

        List<FileUploadResponseDto> responseList = new ArrayList<>();
        int sortOrder = 1;

        if(files != null) {
            for (MultipartFile file : files) {
                // 파일이 없는 경우 건너뜀
                if (file.isEmpty()) {continue;}

                // 파일명 검사
                String fileName = file.getOriginalFilename();
                if (fileName == null || fileName.trim().isEmpty()) {continue;}

                String fileType = file.getContentType();
                long fileSize = file.getSize();

                // 파일 객체 스토리지에 저장
                String fileUrl = null;
                try {
                    fileStorageService.storeFile(file, "post_" + postId);
                } catch (RuntimeException e) {
                    log.error("파일 저장 실패, 건너뜀: " + fileName, e);
                    continue;
                }


                // 파일 메타데이터 저장
                Files saved = filesRepository.save(
                        Files.builder()
                                .post(post)
                                .fileName(fileName)
                                .fileType(fileType)
                                .fileSize(fileSize)
                                .fileUrl(fileUrl)
                                .sortOrder(sortOrder++)
                                .build()
                );

                responseList.add(new FileUploadResponseDto(
                        saved.getId(),
                        post.getId(),
                        saved.getFileName(),
                        saved.getFileType(),
                        saved.getFileSize(),
                        saved.getFileUrl(),
                        saved.getSortOrder(),
                        saved.getCreatedAt()
                ));
            }
        }
        return new RsData<>(
                "200",
                responseList.isEmpty() ? "첨부된 파일이 없습니다." : "파일 업로드 성공",
                responseList
        );
    }

    // 게시글 ID로 파일 조회 서비스
    public RsData<List<FileUploadResponseDto>> getFilesByPostId(Long postId) {
        List<Files> files = filesRepository.findByPostIdOrderBySortOrderAsc(postId);

        List<FileUploadResponseDto> result = files.stream()
                .map(file -> new FileUploadResponseDto(
                        file.getId(),
                        file.getPost().getId(),
                        file.getFileName(),
                        file.getFileType(),
                        file.getFileSize(),
                        file.getFileUrl(),
                        file.getSortOrder(),
                        file.getCreatedAt()
                ))
                .toList();

        return new RsData<>(
                "200",
                result.isEmpty() ? "첨부된 파일이 없습니다." : "파일 목록 조회 성공",
                result
        );
    }


    // 파일 개별 삭제 서비스
    public RsData<Void> deleteFile(Long postId, Long fileId, Long memberId) {
        // TODO: 실제 로그인한 회원 ID는 스프링 시큐리티나 Rq.getActor().getId()에서 받아야 함

        Files file = filesRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("파일이 존재하지 않습니다: " + fileId));

        if (!file.getPost().getId().equals(postId)) {
            throw new IllegalArgumentException("해당 게시글에 속하지 않는 파일입니다: " + fileId);
        }
        if (!file.getPost().getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("해당 파일을 삭제할 권한이 없습니다. " + memberId);
        }

        filesRepository.deleteById(fileId);
        return new RsData("200", "파일 삭제 성공", null);
    }

    // =================== 관리자 전용 서비스 구역 ===================

    // 모든 파일 조회
    public RsData<Page<FileUploadResponseDto>> adminGetAllFiles(Pageable pageable) {
        // TODO: 추후 rq.getActor() 등을 통해 관리자 권한 확인 필요 및 구현
//        if (!rq.getActor().isAdmin()) {
//            return new RsData<>("403", "관리자 권한이 필요합니다.", null);
//        }

        Page<Files> filesPage = filesRepository.findAll(pageable);

        Page<FileUploadResponseDto> dtoPage = filesPage.map(file -> new FileUploadResponseDto(
                        file.getId(),
                        file.getPost().getId(),
                        file.getFileName(),
                        file.getFileType(),
                        file.getFileSize(),
                        file.getFileUrl(),
                        file.getSortOrder(),
                        file.getCreatedAt()
                ));

        return new RsData<>("200", dtoPage.isEmpty() ? "등록된 파일이 없습니다." : "파일 목록 조회 성공", dtoPage);
    }

    // 파일 개별 조회(관리자)
    public RsData<FileUploadResponseDto> adminGetFileById(Long fileId) {
        Files file = filesRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("파일이 존재하지 않습니다: " + fileId));

        FileUploadResponseDto responseDto = new FileUploadResponseDto(
                file.getId(),
                file.getPost().getId(),
                file.getFileName(),
                file.getFileType(),
                file.getFileSize(),
                file.getFileUrl(),
                file.getSortOrder(),
                file.getCreatedAt()
        );

        return new RsData<>("200", "파일 조회 성공 (관리자)", responseDto);
    }

    // 파일 삭제(관리자)
    public RsData<Void> adminDeleteFile(Long fileId) {
        Files file = filesRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("파일이 존재하지 않습니다. " + fileId));

        fileStorageService.deletePhysicalFile(file.getFileUrl());

        filesRepository.deleteById(fileId);
        return new RsData<>("200", "파일 삭제 성공 (관리자)", null);
    }
}
