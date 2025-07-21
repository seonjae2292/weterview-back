package com.example.weterview.controller;

import com.example.weterview.dto.common.ApiResponse;
import com.example.weterview.dto.myPage.request.UpdateNicknameReq;
import com.example.weterview.dto.myPage.response.GetHostedStudyGroupRes;
import com.example.weterview.dto.myPage.response.GetMyPageInfoRes;
import com.example.weterview.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Slf4j
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService myPageService;

    @GetMapping("/info")
    public ApiResponse<GetMyPageInfoRes> getMyPageInfo(@RequestHeader("Authorization") String jwt) {
        return myPageService.getMyPageInfo(jwt);
    }

    // TODO : 추후 S3 적용하여 변경
//    @PatchMapping("/update/profile/image")
//    public ApiResponse<?> updateProfileImage() {
//        return myPageService.updateProfileImage();
//    }

    // 닉네임 변경
    @PatchMapping("/update/nickname")
    public ApiResponse<?> updateNickname(@RequestHeader("Authorization") String jwt, @RequestBody UpdateNicknameReq req) {
        return myPageService.updateNickname(jwt, req);
    }

    // 내가 개설한 스터디 그룹 모집 게시글 조회
    @GetMapping("/hosted-study-groups")
    public ApiResponse<List<GetHostedStudyGroupRes>> getHostedStudyGroups(
            @RequestHeader("Authorization") String jwt,
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize
    ) {
        return myPageService.getHostedStudyGroups(jwt, pageNumber, pageSize);
    }

    // 내가 참여한 스터디 그룹 모집 게시글 조회
    @GetMapping("/joined-study-groups")
    public ApiResponse<?> getJoinedStudyGroups(
            @RequestHeader("Authorization") String jwt,
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return myPageService.getJoinedStudyGroups(jwt, pageNumber, pageSize);
    }
}
