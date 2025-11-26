package com.example.weterview.service;

import com.example.weterview.dto.common.ApiResponse;
import com.example.weterview.dto.myPage.request.UpdateNicknameReq;
import com.example.weterview.dto.myPage.response.*;
import com.example.weterview.entity.*;
import com.example.weterview.enums.studyMembership.JoinEnum;
import com.example.weterview.repository.*;
import com.example.weterview.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final UserRepository userRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final StudyGroupMemberRepository studyGroupMemberRepository;
    private final StudyGroupLikeRepository studyGroupLikeRepository;
    private final StudyGroupCommentRepository studyGroupCommentRepository;
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
        getMyPageInfoRes.setKakaoEmail(user.getKakaoEmail());
        getMyPageInfoRes.setNickname(user.getNickname());
        getMyPageInfoRes.setGender(user.getGender());

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
    public ApiResponse<List<GetHostedStudyGroupRes>> getHostedStudyGroups(
            String jwt, int pageNumber, int pageSize) {
        String kakaoUniqueNumber = jwtUtil.getKakaoUserNumFromToken(jwt);

        Pageable pageable = PageRequest.of(pageNumber, pageSize,
                Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<StudyGroup> hostedStudyGroupPage = studyGroupRepository.findByKakaonum(kakaoUniqueNumber, pageable);

        // 정적 팩토리 메서드 방식
        Page<GetHostedStudyGroupRes> result = hostedStudyGroupPage.map(GetHostedStudyGroupRes::from);
        return ApiResponse.ok(result.getContent(), "자신이 개설한 스터디 그룹 조회");
    }

    // 내가 참여한 스터디 그룹 모집 게시글 조회
    public ApiResponse<Page<GetJoinedStudyGroupRes>> getJoinedStudyGroups(String jwt, int pageNumber, int pageSize) {
        String kakaoUniqueNumber = jwtUtil.getKakaoUserNumFromToken(jwt);
        User user = userRepository.findByKakaoUserNumber(kakaoUniqueNumber)
                .orElseThrow(() -> new IllegalArgumentException("없는 사용자 입니다"));

        Pageable pageable = PageRequest.of(pageNumber, pageSize,
                Sort.by(Sort.Direction.DESC, "appliedAt"));

        Page<StudyGroupMember> members = studyGroupMemberRepository.findStudyGroupMembersByUser(user, pageable);

        Page<GetJoinedStudyGroupRes> result = members.map(GetJoinedStudyGroupRes::from);
        return ApiResponse.ok(result, "자신이 참여한 스터디 그룹 조회");
    };

    /// 스터디 그룹 신청 수락
    public ApiResponse<?> acceptJoinStudyGroup(Long userId, String studyGroupId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 입니다"));
        StudyGroup studyGroup = studyGroupRepository.findById(Long.parseLong(studyGroupId))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스터디 그룹 ID 입니다."));

        StudyGroupMember studyGroupMember =
                studyGroupMemberRepository.findStudyGroupMemberByUserAndStudyGroup(user, studyGroup)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않습니다"));

        studyGroupMember.setJoin(JoinEnum.ACCEPT);
        studyGroupMember.setAcceptedAt(LocalDateTime.now());

        studyGroupMemberRepository.save(studyGroupMember);

        return ApiResponse.ok("승인했습니다.");
    }

    /// 스터디 그룹 신청 거절
    public ApiResponse<?> rejectJoinStudyGroup(Long userId, String studyGroupId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 입니다"));
        StudyGroup studyGroup = studyGroupRepository.findById(Long.parseLong(studyGroupId))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스터디 그룹 ID 입니다."));

        StudyGroupMember studyGroupMember =
                studyGroupMemberRepository.findStudyGroupMemberByUserAndStudyGroup(user, studyGroup)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않습니다"));

        studyGroupMember.setJoin(JoinEnum.REFUSE);
        studyGroupMember.setRefusedAt(LocalDateTime.now());

        studyGroupMemberRepository.save(studyGroupMember);

        return ApiResponse.ok("거절했습니다");
    }

    // 내가 좋아요한 게시글
    public ApiResponse<?> getLikePost(String jwt, int pageNumber, int pageSize) {
        String kakaoUserNumber = jwtUtil.getKakaoUserNumFromToken(jwt);
        User user = userRepository.findByKakaoUserNumber(kakaoUserNumber)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 입니다"));

        Pageable pageable = PageRequest.of(pageNumber, pageSize,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<StudyGroupLike> studyGroupLikes =
                studyGroupLikeRepository.findStudyGroupLikesByUser(user, pageable);

        Page<GetLikedPostRes> result = studyGroupLikes.map(GetLikedPostRes::from);

        return ApiResponse.ok(result, "success");
    }

    // 댓글단 게시글 조회
    public ApiResponse<Page<GetCommentedStudyGroupRes>> getCommentedPost(String jwt, int pageNumber, int pageSize) {
        String kakaoUserNumber = jwtUtil.getKakaoUserNumFromToken(jwt);
        User user = userRepository.findByKakaoUserNumber(kakaoUserNumber)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 입니다"));

        Pageable pageable = PageRequest.of(pageNumber, pageSize,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<StudyGroupComment> studyGroupComments =
                studyGroupCommentRepository.findStudyGroupCommentsByUser(user, pageable);

        Page<GetCommentedStudyGroupRes> result = studyGroupComments.map(GetCommentedStudyGroupRes::from);

        return ApiResponse.ok(result, "success");
    }
}
