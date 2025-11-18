package com.example.weterview.service;

import com.example.weterview.dto.common.ApiResponse;
import com.example.weterview.dto.myPage.request.UpdateNicknameReq;
import com.example.weterview.dto.myPage.response.GetHostedStudyGroupRes;
import com.example.weterview.dto.myPage.response.GetJoinedStudyGroupRes;
import com.example.weterview.dto.myPage.response.GetMyPageInfoRes;
import com.example.weterview.entity.StudyGroup;
import com.example.weterview.entity.StudyMembership;
import com.example.weterview.entity.User;
import com.example.weterview.enums.studyMembership.JoinEnum;
import com.example.weterview.repository.StudyGroupRepository;
import com.example.weterview.repository.StudyMembershipRepository;
import com.example.weterview.repository.UserRepository;
import com.example.weterview.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final UserRepository userRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final StudyMembershipRepository studyMembershipRepository;

    private final JwtUtil jwtUtil;

    // 사용자 mypage 정보 가져오기
    public ApiResponse<GetMyPageInfoRes> getMyPageInfo(String jwt) {
        // 카카로 고유 number
        String kakaoUniqueNumber = jwtUtil.getKakaoUserNumFromToken(jwt);
        User user = userRepository.findByKakaoUserNumber(kakaoUniqueNumber)
                .orElseThrow(() -> new IllegalArgumentException("없는 사용자 입니다."));

        GetMyPageInfoRes getMyPageInfoRes = new GetMyPageInfoRes();
        getMyPageInfoRes.setCreatedAt(user.getCreatedAt());
        getMyPageInfoRes.setUpdatedAt(user.getUpdatedAt());
        getMyPageInfoRes.setEmail(user.getEmail());
        getMyPageInfoRes.setNickname(user.getNickname());

        return ApiResponse.ok(getMyPageInfoRes, "사용자 정보 반환");
    }

    // 닉네임 변경
    public ApiResponse<?> updateNickname(String jwt, UpdateNicknameReq req) {
        String kakaoUniqueNumber = jwtUtil.getKakaoUserNumFromToken(jwt);
        User user = userRepository.findByKakaoUserNumber(kakaoUniqueNumber)
                .orElseThrow(() -> new IllegalArgumentException("없는 사용자 입니다"));

        user.setNickname(req.getNickname());

        userRepository.save(user);

        return ApiResponse.ok(null, "닉네임 변경 성공");
    }

    // 내가 개설한 스터디 그룹 모집 게시글 조회
    public ApiResponse<List<GetHostedStudyGroupRes>> getHostedStudyGroups(String jwt, int pageNumber, int pageSize) {
        String kakaoUniqueNumber = jwtUtil.getKakaoUserNumFromToken(jwt);
        User user = userRepository.findByKakaoUserNumber(kakaoUniqueNumber)
                .orElseThrow(() -> new IllegalArgumentException("없는 사용자 입니다"));

        Pageable pageable = PageRequest.of(pageNumber, pageSize,
                Sort.by("createdAt").descending());
        Page<StudyGroup> hostedStudyGroupPage = studyGroupRepository.findByUser(user, pageable);

        // 정적 팩토리 메서드 방식
        Page<GetHostedStudyGroupRes> result = hostedStudyGroupPage.map(GetHostedStudyGroupRes::from);
        return ApiResponse.ok(result.getContent(), "자신이 개설한 스터디 그룹 조회");
    }

    // TODO : 개선해야하는 부분
    // 내가 참여한 스터디 그룹 모집 게시글 조회
    public ApiResponse<?> getJoinedStudyGroups(String jwt, int pageNumber, int pageSize) {
        String kakaoUniqueNumber = jwtUtil.getKakaoUserNumFromToken(jwt);
        User user = userRepository.findByKakaoUserNumber(kakaoUniqueNumber)
                .orElseThrow(() -> new IllegalArgumentException("없는 사용자 입니다"));

        Pageable pageable = PageRequest.of(pageNumber, pageSize,
                Sort.by("createdAt").descending());

        // 쿼리 한번
        Page<StudyGroup> joinedStudyGroupList = studyMembershipRepository
                .findByUserWithStudyGroup(user, pageable);

        Page<GetJoinedStudyGroupRes> result = joinedStudyGroupList.map(GetJoinedStudyGroupRes::from);
        return ApiResponse.ok(result.getContent(), "자신이 참여한 스터디 그룹 조회");
    };

    /// 스터디 그룹 신청 수락
    public ApiResponse<?> acceptJoinStudyGroup(String studyGroupId) {
        StudyMembership studyMembership =
                studyMembershipRepository.findById(Long.parseLong(studyGroupId))
                        .orElseThrow(() -> new IllegalArgumentException("개설되지 않은 스터디 그룹 입니다."));

        studyMembership.setJoin(JoinEnum.ACCEPT);

        studyMembershipRepository.save(studyMembership);

        return ApiResponse.ok("승인했습니다.");
    }

    /// 스터디 그룹 신청 거절
    public ApiResponse<?> rejectJoinStudyGroup(String studyGroupId) {
        StudyMembership studyMembership =
                studyMembershipRepository.findById(Long.parseLong(studyGroupId))
                .orElseThrow(() -> new IllegalArgumentException("개설되지 않은 스터디 그룹 입니다."));

        studyMembership.setJoin(JoinEnum.REFUSE);

        studyMembershipRepository.save(studyMembership);

        return ApiResponse.ok("거절했습니다");
    }
}
