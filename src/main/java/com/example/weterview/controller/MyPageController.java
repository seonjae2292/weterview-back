package com.example.weterview.controller;

import com.example.weterview.config.CustomUserDetails;
import com.example.weterview.dto.common.ApiResponse;
import com.example.weterview.dto.myPage.request.UpdateNicknameReq;
import com.example.weterview.dto.myPage.response.GetHostedStudyGroupRes;
import com.example.weterview.dto.myPage.response.GetJoinedStudyGroupRes;
import com.example.weterview.dto.myPage.response.MyPageRes;
import com.example.weterview.service.MyPageService;
import com.example.weterview.service.StudyGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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

    // 스터디 그룹 상세 정보 조회
    @GetMapping("/detail/{studyGroupId}")
    public ApiResponse<?> getStudyGroupDetail(@PathVariable String studyGroupId) {
        return studyGroupService.getStudyGroupDetail(studyGroupId);
    }

    // 스터디 그룹 신청자 조회

    /// 스터디 그룹 상세 정보에서 자신이 개설한 정보 확인
    @GetMapping("/applied/list/{studyGroupId}")
    public ApiResponse<?> getAppliedStudyGroup(@PathVariable String studyGroupId) {
        return studyGroupService.getAppliedStudyGroup(studyGroupId);
    }

    // 스터디 그룹 신청 수락
    @PostMapping("/accept/{userId}/{studyGroupId}")
    public ApiResponse<?> acceptJoinStudyGroup(
            @PathVariable Long userId, @PathVariable String studyGroupId) {
        return myPageService.acceptJoinStudyGroup(userId, studyGroupId);
    }

    // 스터디 그룹 신청 거절
    @PostMapping("/reject/{userId}/{studyGroupId}")
    public ApiResponse<?> rejectJoinStudyGroup(
            @PathVariable Long userId, @PathVariable String studyGroupId) {
        return myPageService.rejectJoinStudyGroup(userId, studyGroupId);
    }

    // 좋아요한 게시글 조회
    @GetMapping("/likes/posts")
    public ApiResponse<?> getLikePost(
            @RequestHeader("Authorization") String jwt,
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return myPageService.getLikePost(jwt, pageNumber - 1, pageSize);
    }

    // 댓글단 게시글 조회
    @GetMapping("/commented/posts")
    public ApiResponse<?> getCommentedPost(
            @RequestHeader("Authorization") String jwt,
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return myPageService.getCommentedPost(jwt, pageNumber - 1, pageSize);
    }
}
