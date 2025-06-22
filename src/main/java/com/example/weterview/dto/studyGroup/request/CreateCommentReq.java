package com.example.weterview.dto.studyGroup.request;

import lombok.Data;

@Data
public class CreateCommentReq {
    private String studyGroupId;
    private String contents;
}
