package com.example.weterview.controller;

import com.example.weterview.dto.common.ApiResponse;
import com.example.weterview.dto.studyGroup.request.*;
import com.example.weterview.service.StudyGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/studygroup")
@RequiredArgsConstructor
public class StudyGroupController {
    private final StudyGroupService studyGroupService;

    // 스터디 그룹 모집 게시글 생성
    @PostMapping("/create")
    public ApiResponse<?> createStudyGroup(@RequestBody CreateStudyGroupReq req) {
        return studyGroupService.createStudyGroup(req);
    }

    // 스터디 그룹 모집 게시글 검색 조회
    @GetMapping("/get")
    public ApiResponse<?> getStudyGroup(@ModelAttribute @Valid GetStudyGroupReq req) {
        return studyGroupService.getStudyGroup(req);
    }

    // 스터디 그룹 모집 게시글 단건 조회
    @GetMapping("/get/{id}")
    public ApiResponse<GetStudyGroupByIdRes> getStudyGroupById(@PathVariable String id) {
        return studyGroupService.getStudyGroupById(id);
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

    // 스터디 그룹 모집 게시글 참여하기
    @PostMapping("/join")
    public ApiResponse<?> joinStudyGroup(
            @RequestBody JoinStudyGroupReq req,
            @RequestHeader("Authorization") String jwt) {
        return studyGroupService.joinStudyGroup(req, jwt);
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
    @DeleteMapping("/{studyGroupId}/likes")
    public ApiResponse<?> unlikeStudyGroup(
            @PathVariable String studyGroupId,
            @RequestHeader("Authorization") String jwt){
        return studyGroupService.unlikeStudyGroup(studyGroupId, jwt);
    }
}
