package com.back.domain.files.files.service;

import com.back.domain.files.files.dto.FileUploadResponseDto;
import com.back.domain.files.files.entity.Files;
import com.back.domain.files.files.repository.FilesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilesService {

    private final FilesRepository filesRepository;
    private final PostRepository postRepository;

    public List<FileUploadResponseDto> uploadFiles(Long postId, MultipartFile[] files) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다: " + postId));

        List<FileUploadResponseDto> responseList = new ArrayList<>();

        int sortOrder = 1;
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            String fileType = file.getContentType();
            long fileSize = file.getSize();

            // TODO: 실제 파일 저장 후 URL 생성(임시 URL 사용)
            String fileUrl = "http://example.com/uploads/test.png" + fileName;

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
        return responseList;
    }
}
