package com.example.weterview.dto.studyGroup.response;

import com.example.weterview.entity.StudyGroupComment;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StudyGroupCommentRes {
    private String content;
    private LocalDateTime createdAt;
    private String nickname;

    public static StudyGroupCommentRes from(StudyGroupComment studyGroupComment) {
        return StudyGroupCommentRes.builder()
                .content(studyGroupComment.getContent())
                .nickname(studyGroupComment.getUser().getNickname())
                .createdAt(studyGroupComment.getCreatedAt())
                .build();
    }
}
