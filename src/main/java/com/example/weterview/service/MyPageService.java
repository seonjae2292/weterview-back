package com.example.weterview.service;

import com.example.weterview.config.CustomUserDetails;
import com.example.weterview.dto.common.ApiResponse;
import com.example.weterview.dto.myPage.request.UpdateNicknameReq;
import com.example.weterview.dto.myPage.response.*;
import com.example.weterview.entity.*;
import com.example.weterview.enums.ErrorCode;
import com.example.weterview.enums.studyMembership.JoinEnum;
import com.example.weterview.exception.CustomException;
import com.example.weterview.repository.*;
import com.example.weterview.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {
    private final UserRepository userRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final StudyGroupMemberRepository studyGroupMemberRepository;
    private final StudyGroupLikeRepository studyGroupLikeRepository;
    private final StudyGroupCommentRepository studyGroupCommentRepository;
    private final JwtUtil jwtUtil;

    // 사용자 mypage 정보 가져오기
    public MyPageRes getMyPageInfo(User user) {
        return MyPageRes.from(user);
    }

    // 닉네임 변경
    @Transactional
    public void updateNickname(User principalUser, UpdateNicknameReq req) {
        String newNickname = req.getNickname();

        // 닉네임 중복 검증
        if (userRepository.existsByNickname(newNickname)) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }

        // 영속성 컨텐스트 안으로 엔티티 가져오기
        // principalUser는 SecurityFilter가 만든 준영속 객체이다
        User user = userRepository.findById(principalUser.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // user는 영속 상태
        // 값 변경 시에 트랜잭션 종료 시 알아서 update 쿼리가 나간다.
        user.changeNickname(newNickname);
    }

    // 내가 개설한 스터디 그룹 모집 게시글 조회
    public Page<GetHostedStudyGroupRes> getHostedStudyGroups(
            User principalUser, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<StudyGroup> hostedStudyGroupPage = studyGroupRepository.findByUser(principalUser, pageable);

        // 정적 팩토리 메서드 방식
        return hostedStudyGroupPage.map(GetHostedStudyGroupRes::from);
    }

    // 내가 참여한 스터디 그룹 모집 게시글 조회
    public Page<GetJoinedStudyGroupRes> getJoinedStudyGroups(
            User principalUser, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize,
                Sort.by(Sort.Direction.DESC, "appliedAt"));

        Page<StudyGroupMember> members =
                studyGroupMemberRepository.findStudyGroupMembersByUser(principalUser, pageable);

        Page<GetJoinedStudyGroupRes> result = members.map(GetJoinedStudyGroupRes::from);

        return result;
    };

    /// 스터디 그룹 신청 수락
    @Transactional
    public void acceptJoinStudyGroup(User principalUser, Long userId, long studyGroupId) {
        StudyGroup studyGroup = studyGroupRepository.findById(studyGroupId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_NOT_FOUND));

        studyGroup.validateHost(principalUser);

        // 영속 객체
        StudyGroupMember studyGroupMember =
                studyGroupMemberRepository.findByUserIdAndStudyGroupId(userId, studyGroup)
                        .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_MEMBER_NOT_FOUND));

        studyGroupMember.acceptJoin();
    }

    /// 스터디 그룹 신청 거절
    @Transactional
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
                studyGroupLikeRepository.findByUserAndIsLiked(user, true, pageable);

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
