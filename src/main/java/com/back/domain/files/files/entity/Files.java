package com.back.domain.files.files.entity;

import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "files")
public class Files extends BaseEntity {

    // 연관 게시글 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false)
    private int fileSize;

    @Column(nullable = false)
    private String fileUrl;

    // 정렬 순서
    @Column(nullable = false)
    private Integer sortOrder;
}
