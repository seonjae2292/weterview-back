package com.example.weterview.controller;

import com.example.weterview.config.CustomUserDetails;
import com.example.weterview.dto.common.ApiResponse;
import com.example.weterview.dto.studyGroup.request.*;
import com.example.weterview.dto.studyGroup.response.StudyGroupRes;
import com.example.weterview.entity.StudyGroup;
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

    // 스터디 그룹 모집 게시글 생성
    @PostMapping("/create")
    public ApiResponse<?> createStudyGroup(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody CreateStudyGroupReq req) {
        studyGroupService.createStudyGroup(customUserDetails.getUser(), req);
        return ApiResponse.success();
    }

    // [검색] 스터디 그룹 조회
    @GetMapping("/get")
    public ApiResponse<?> getStudyGroup(
            @ModelAttribute @Valid GetStudyGroupReq req) {
        Page<StudyGroupRes> response = studyGroupService.getStudyGroup(req);
        return ApiResponse.success(response);
    }

    // 스터디 그룹 모집 게시글 단건 조회
    @GetMapping("/get/{id}")
    public ApiResponse<GetStudyGroupByIdRes> getStudyGroupById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        GetStudyGroupByIdRes response = studyGroupService.getStudyGroupById(id, customUserDetails.getUser());
        return ApiResponse.success(response);
    }

    // 스터디 그룹 모집 게시글 수정
    @PatchMapping("/update/{id}")
    public ApiResponse<?> updateStudyGroup(
            @PathVariable String id,
            @RequestBody @Valid UpdateStudyGroupReq req) {
        return studyGroupService.updateStudyGroup(id, req);
    }

    // 스터디 그룹 모집 게시글 삭제
    @DeleteMapping("/delete/{id}")
    public ApiResponse<?> deleteStudyGroup(@PathVariable String id) {
        return studyGroupService.deleteStudyGroup(id);
    }

    // 스터디 그룹 참여 신청
    @PostMapping("/join/{studyGroupId}")
    public ApiResponse<?> joinStudyGroup(
            @PathVariable String studyGroupId,
            @RequestHeader("Authorization") String jwt) {
        return studyGroupService.joinStudyGroup(studyGroupId, jwt);
    }

    // 댓글 추가
    @PostMapping("/create/comment")
    public ApiResponse<?> createComment(
            @RequestBody CreateCommentReq req,
            @RequestHeader("Authorization") String jwt) {
        return studyGroupService.createComment(req, jwt);
    }

    // 스터디 그룹 모집 게시글 댓글 조회
    @GetMapping("/get/comment/{studyGroupId}")
    public ApiResponse<?> getComment(@PathVariable String studyGroupId) {
        return studyGroupService.getComment(studyGroupId);
    }

    // 스터디 그룹 모집 게시글 좋아요
    @PostMapping("/{studyGroupId}/likes")
    public ApiResponse<?> likeStudyGroup(
            @PathVariable String studyGroupId,
            @RequestHeader("Authorization") String jwt) {
        return studyGroupService.likeStudyGroup(studyGroupId, jwt);
    }

    // 스터디 그룹 모집 게시글 좋아요 취소
    @PostMapping("/{studyGroupId}/unlikes")
    public ApiResponse<?> unlikeStudyGroup(
            @PathVariable String studyGroupId,
            @RequestHeader("Authorization") String jwt) {
        return studyGroupService.unlikeStudyGroup(studyGroupId, jwt);
    }

    // 인기있는 스터디 그룹 조회
    @GetMapping("/popular")
    public ApiResponse<?> getPopularStudyGroup(
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize
    ) {
        return studyGroupService.getPopularStudyGroup(pageNumber - 1, pageSize);
    }

    // 최신 스터디 그룹 조회
    @GetMapping("/latest")
    public ApiResponse<List<StudyGroupRes>> getLatestStudyGroup(
            @RequestParam(value = "count", defaultValue = "3") int count) {
        return studyGroupService.getLatestStudyGroup(count);
    }
}
