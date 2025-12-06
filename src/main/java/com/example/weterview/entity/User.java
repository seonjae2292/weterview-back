package com.example.weterview.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, name = "kakao_user_number")
    private String kakaoUserNumber;

    @Column(nullable = false,name = "nickname", length = 100)
    private String nickname;

    @Column(nullable = false,name = "kakao_email", length = 100)
    private String kakaoEmail;

    @Column(nullable = false, name = "gender")
    private String gender;

    @Column(nullable = false,name = "created_at", updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "withdrawn_at", updatable = false)
    private LocalDateTime withdrawnAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void changeNickname(String newNickname) {
        this.nickname = newNickname;
    }
}
