package com.example.weterview.service;

import com.example.weterview.dto.common.ApiResponse;
import com.example.weterview.dto.studyGroup.request.CreateCommentReq;
import com.example.weterview.dto.studyGroup.response.CommentRes;
import com.example.weterview.entity.StudyGroup;
import com.example.weterview.entity.StudyGroupComment;
import com.example.weterview.entity.User;
import com.example.weterview.enums.ErrorCode;
import com.example.weterview.exception.CustomException;
import com.example.weterview.repository.StudyGroupCommentRepository;
import com.example.weterview.repository.StudyGroupRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class StudyGroupCommentService {
    private final StudyGroupRepository studyGroupRepository;
    private final StudyGroupCommentRepository studyGroupCommentRepository;

    // 댓글 추가
    @Transactional
    public void createComment(User principalUser, CreateCommentReq req) {
        StudyGroup studyGroup = studyGroupRepository.findById(Long.parseLong(req.getStudyGroupId()))
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_NOT_FOUND));

        StudyGroupComment studyGroupComment = StudyGroupComment.builder()
                .user(principalUser)
                .studyGroup(studyGroup)
                .content(req.getContents())
                .createdAt(LocalDateTime.now())
                .build();

        studyGroupCommentRepository.save(studyGroupComment);
    }

    // 댓글 조회
    public ApiResponse<List<CommentRes>> getComment(String studyGroupId) {
        List<StudyGroupComment> comments =
                studyGroupCommentRepository.findByStudyGroupId(Long.parseLong(studyGroupId));

        List<CommentRes> result = List.of();

        if (!comments.isEmpty()) {
            result = comments.stream()
                    .map(item ->
                            new CommentRes(item.getContent(), item.getCreatedAt(), item.getUser().getNickname()))
                    .toList();
        }

        return ApiResponse.ok(result, "댓글 조회 성공");
    }
}
