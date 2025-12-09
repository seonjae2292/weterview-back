package com.example.weterview.controller;

import com.example.weterview.config.CustomUserDetails;
import com.example.weterview.dto.common.ApiResponse;
import com.example.weterview.dto.myPage.request.UpdateNicknameReq;
import com.example.weterview.dto.myPage.response.*;
import com.example.weterview.dto.studyGroup.response.StudyGroupApplyMemberRes;
import com.example.weterview.service.MyPageService;
import com.example.weterview.service.StudyGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService myPageService;
    private final StudyGroupService studyGroupService;

    @GetMapping("/info")
    public ApiResponse<MyPageRes> getMyPageInfo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        MyPageRes response = myPageService.getMyPageInfo(customUserDetails.getUser());
        return ApiResponse.success(response);
    }

    // TODO : 추후 S3 적용하여 변경
//    @PatchMapping("/update/profile/image")
//    public ApiResponse<?> updateProfileImage() {
//        return myPageService.updateProfileImage();
//    }

    // 닉네임 변경
    @PatchMapping("/update/nickname")
    public ApiResponse<?> updateNickname(
            @RequestBody @Valid UpdateNicknameReq req,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        myPageService.updateNickname(customUserDetails.getUser(), req);
        return ApiResponse.success();
    }

    // 내가 개설한 스터디 그룹 모집 게시글 조회
    @GetMapping("/hosted-study-groups")
    public ApiResponse<?> getHostedStudyGroups(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize
    ) {
        Page<GetHostedStudyGroupRes> response = myPageService.getHostedStudyGroups(
                customUserDetails.getUser(), pageNumber - 1, pageSize);
        return ApiResponse.success(response);
    }

    // 내가 참여한 스터디 그룹 모집 게시글 조회
    @GetMapping("/joined-study-groups")
    public ApiResponse<?> getJoinedStudyGroups(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        Page<GetJoinedStudyGroupRes> response =
                myPageService.getJoinedStudyGroups(customUserDetails.getUser(), pageNumber - 1, pageSize);
        return ApiResponse.success(response);
    }


    // 스터디 그룹 신청 수락
    @PostMapping("/accept/{userId}/{studyGroupId}")
    public ApiResponse<?> acceptJoinStudyGroup(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long userId, @PathVariable long studyGroupId) {
        myPageService.acceptJoinStudyGroup(
                customUserDetails.getUser(), userId, studyGroupId);
        return ApiResponse.success();
    }

    // 스터디 그룹 신청 거절
    @PostMapping("/reject/{userId}/{studyGroupId}")
    public ApiResponse<?> rejectJoinStudyGroup(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long userId, @PathVariable Long studyGroupId) {
        myPageService.refuseJoinStudyGroup(
                customUserDetails.getUser(), userId, studyGroupId);
        return ApiResponse.success();
    }

    // 내가 좋아요한 게시글 조회
    @GetMapping("/likes/posts")
    public ApiResponse<Page<GetLikedPostRes>> getLikePost(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        Page<GetLikedPostRes> response =
                myPageService.getLikedPosts(customUserDetails.getUser(), pageNumber - 1, pageSize);
        return ApiResponse.success(response);
    }

    // 댓글단 게시글 조회
    @GetMapping("/commented/posts")
    public ApiResponse<Page<GetCommentedStudyGroupRes>> getCommentedPost(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        Page<GetCommentedStudyGroupRes> response =
                myPageService.getCommentedPosts(customUserDetails.getUser(), pageNumber, pageSize);
        return ApiResponse.success(response);
    }

    // 스터디 그룹에 신청한 신청자 목록 조회
    @GetMapping("/applied/list/{studyGroupId}")
    public ApiResponse<Page<StudyGroupApplyMemberRes>> getAppliedStudyGroup(
            @PathVariable Long studyGroupId,
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        Page<StudyGroupApplyMemberRes> response =
                myPageService.getAppliedStudyGroup(studyGroupId, pageNumber - 1, pageSize);
        return ApiResponse.success(response);
    }
}
