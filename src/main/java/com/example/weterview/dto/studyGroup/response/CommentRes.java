package com.example.weterview.dto.studyGroup.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentRes {
    private String content;
    private LocalDateTime createdAt;
    private String nickname;
}
