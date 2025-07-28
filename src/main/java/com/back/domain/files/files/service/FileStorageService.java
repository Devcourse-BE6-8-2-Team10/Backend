package com.back.domain.files.files.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.apache.naming.SelectorContext.prefix;

@Slf4j
@Service
public class FileStorageService {

    // === 로컬 저장 경로 ===
    private final String UPLOAD_DIR = System.getProperty("user.home") + "/Downloads/uploads";

    // 첨부파일 저장
    public String storeFile(MultipartFile file, String subFolder) {
        try {
            // 디렉토리 생성
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if(!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }

            // 저장된 파일명 생성
            String originalFileName = file.getOriginalFilename();
            String extension = getExtension(originalFileName);
            String fileName = prefix + "_" + System.currentTimeMillis() + extension;

            Path filePath = uploadPath.resolve(fileName);
            file.transferTo(filePath.toFile());

            // 접근 가능한 URL 반환
            return "file:///" + filePath.toAbsolutePath().toString().replace("\\", "/");

            // 추후 S3나 다른 스토리지로 변경 시 URL 형식에 맞게 수정 필요
            // return "http://storage-cdn.com/" + fileName;
        } catch (IOException e) {
            log.error("파일 저장 실패", e);
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.");
        }
    }

    // 파일 삭제
    public void deletePhysicalFile(String fileUrl) {
        try {
            if (fileUrl == null || !fileUrl.startsWith("file:///")) return;

            String localPath = fileUrl.replace("file:///", "/").replace("%20", " ");
            File file = new File(localPath);
            if (file.exists()) {
                if (!file.delete()) {
                    log.warn("파일 삭제 실패: " + localPath);
                }
            }
        } catch (Exception e) {
            log.error("파일 삭제 중 오류 발생", e);
        }
    }

    // 파일 확장자 추출
    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        return dotIndex != -1 ? fileName.substring(dotIndex) : "";
    }
}
