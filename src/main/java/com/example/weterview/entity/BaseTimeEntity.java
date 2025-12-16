package com.example.weterview.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass // 1. JPA Entity 클래스들이 이 클래스를 상속할 경우 필드들도 컬럼으로 인식하게 함
@EntityListeners(AuditingEntityListener.class) // 2. JPA Auditing 기능 포함 (자동 시간 매핑)
public class BaseTimeEntity {
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
