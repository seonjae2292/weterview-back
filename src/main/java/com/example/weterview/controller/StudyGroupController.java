package com.example.weterview.controller;

import com.example.weterview.config.CustomUserDetails;
import com.example.weterview.dto.common.ApiResponse;
import com.example.weterview.dto.studyGroup.request.*;
import com.example.weterview.dto.studyGroup.response.StudyGroupCommentRes;
import com.example.weterview.dto.common.response.StudyGroupRes;
import com.example.weterview.entity.User;
import com.example.weterview.service.StudyGroupCommentService;
import com.example.weterview.service.StudyGroupLikeService;
import com.example.weterview.service.StudyGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/studygroup")
@RequiredArgsConstructor
public class StudyGroupController {
    private final StudyGroupService studyGroupService;
    private final StudyGroupCommentService studyGroupCommentService;
    private final StudyGroupLikeService StudyGroupLikeService;

    // 스터디 그룹 모집 게시글 생성
    @PostMapping("/create")
    public ApiResponse<StudyGroupRes> createStudyGroup(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody CreateStudyGroupReq req) {
        StudyGroupRes result = studyGroupService.createStudyGroup(customUserDetails.getUser(), req);
        return ApiResponse.success(result);
    }

    // [검색] 스터디 그룹 조회
    @GetMapping("/get")
    public ApiResponse<?> getStudyGroup(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @ModelAttribute @Valid GetStudyGroupReq req) {
        User principalUser = customUserDetails != null ? customUserDetails.getUser() : null;
        Page<StudyGroupRes> response = studyGroupService.getStudyGroup(principalUser, req);
        return ApiResponse.success(response);
    }

    // 스터디 그룹 모집 게시글 단건 조회
    @GetMapping("/get/{id}")
    public ApiResponse<StudyGroupRes> getStudyGroupById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        StudyGroupRes response = studyGroupService.getStudyGroupById(id, customUserDetails.getUser());
        return ApiResponse.success(response);
    }

    // 스터디 그룹 모집 게시글 수정
    @PatchMapping("/update/{id}")
    public ApiResponse<?> updateStudyGroup(
            @PathVariable Long id, @RequestBody @Valid UpdateStudyGroupReq req) {
        studyGroupService.updateStudyGroup(id, req);
        return ApiResponse.success();
    }

    // 스터디 그룹 모집 게시글 삭제
    @DeleteMapping("/delete/{id}")
    public ApiResponse<?> deleteStudyGroup(@PathVariable Long id) {
        studyGroupService.deleteStudyGroup(id);
        return ApiResponse.success();
    }

    // 스터디 그룹 참여 신청
    @PostMapping("/join/{studyGroupId}")
    public ApiResponse<?> joinStudyGroup(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long studyGroupId) {
        studyGroupService.joinStudyGroup(customUserDetails.getUser(), studyGroupId);
        return ApiResponse.success();
    }

    // 댓글 추가
    @PostMapping("/create/comment")
    public ApiResponse<?> createComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody CreateCommentReq req) {
        studyGroupCommentService.createComment(customUserDetails.getUser(), req);
        return ApiResponse.success();
    }

    // 스터디 그룹 게시글 댓글 조회
    @GetMapping("/get/comment/{studyGroupId}")
    public ApiResponse<List<StudyGroupCommentRes>> getComment(@PathVariable Long studyGroupId) {
        List<StudyGroupCommentRes> response = studyGroupCommentService.getComment(studyGroupId);
        return ApiResponse.success(response);
    }

    // 스터디 그룹 모집 게시글 좋아요
    @PostMapping("/{studyGroupId}/likes")
    public ApiResponse<?> likeStudyGroup(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String studyGroupId) {
        StudyGroupLikeService.likeStudyGroup(customUserDetails.getUser(), studyGroupId);
        return ApiResponse.success();
    }

    // 인기있는 스터디 그룹 조회
    @GetMapping("/popular")
    public ApiResponse<Page<StudyGroupRes>> getPopularStudyGroup(
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize
    ) {
        Page<StudyGroupRes> response =
                studyGroupService.getPopularStudyGroup(pageNumber - 1, pageSize);
        return ApiResponse.success(response);
    }

    // 최신 스터디 그룹 조회
    @GetMapping("/latest")
    public ApiResponse<List<StudyGroupRes>> getLatestStudyGroup(
            @RequestParam(value = "count", defaultValue = "3") int count) {
        List<StudyGroupRes> response = studyGroupService.getLatestStudyGroup(count);
        return ApiResponse.success(response);
    }
}
