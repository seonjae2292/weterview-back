package com.example.weterview.service;

import com.example.weterview.dto.myPage.request.UpdateNicknameReq;
import com.example.weterview.dto.myPage.response.*;
import com.example.weterview.dto.studyGroup.response.StudyGroupApplyMemberRes;
import com.example.weterview.dto.common.response.StudyGroupRes;
import com.example.weterview.entity.*;
import com.example.weterview.entity.studyGroup.StudyGroup;
import com.example.weterview.enums.ResultCode;
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
            throw new CustomException(ResultCode.DUPLICATE_NICKNAME);
        }

        // 영속성 컨텐스트 안으로 엔티티 가져오기
        // principalUser는 SecurityFilter가 만든 준영속 객체이다
        User user = userRepository.findById(principalUser.getId())
                .orElseThrow(() -> new CustomException(ResultCode.USER_NOT_FOUND));

        // user는 영속 상태
        // 값 변경 시에 트랜잭션 종료 시 알아서 update 쿼리가 나간다.
        user.changeNickname(newNickname);
    }

    // 내가 개설한 스터디 그룹 모집 게시글 조회
    public Page<StudyGroupRes> getHostedStudyGroups(
            User principalUser, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<StudyGroup> hostedStudyGroupPage = studyGroupRepository.findByUser(principalUser, pageable);

        // 정적 팩토리 메서드 방식
        return hostedStudyGroupPage.map(StudyGroupRes::from);
    }

    // 내가 참여한 스터디 그룹 모집 게시글 조회
    public Page<StudyGroupRes> getJoinedStudyGroups(
            User principalUser, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize,
                Sort.by(Sort.Direction.DESC, "appliedAt"));

        Page<StudyGroup> joinedStudyGroupList =
                studyGroupRepository.findJoinedByUserByUser(principalUser, pageable);

        return joinedStudyGroupList.map(StudyGroupRes::from);
    };

    /// 스터디 그룹 신청 수락
    @Transactional
    public void acceptJoinStudyGroup(User principalUser, Long userId, long studyGroupId) {
        StudyGroup studyGroup = studyGroupRepository.findById(studyGroupId)
                .orElseThrow(() -> new CustomException(ResultCode.STUDY_GROUP_NOT_FOUND));

        // 영속 객체
        StudyGroupMember studyGroupMember =
                studyGroupMemberRepository.findByUserIdAndStudyGroupId(userId, studyGroup)
                        .orElseThrow(() -> new CustomException(ResultCode.STUDY_GROUP_MEMBER_NOT_FOUND));

        studyGroupMember.acceptJoin();
    }

    /// 스터디 그룹 신청 거절
    @Transactional
    public void refuseJoinStudyGroup(User principalUser, Long userId, Long studyGroupId) {
        StudyGroup studyGroup = studyGroupRepository.findById(studyGroupId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스터디 그룹 ID 입니다."));

        StudyGroupMember studyGroupMember =
                studyGroupMemberRepository.findByUserIdAndStudyGroupId(userId, studyGroup)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않습니다"));
        studyGroupMember.refuseJoin();
    }

    // 내가 좋아요한 게시글 조회
    public Page<StudyGroupRes> getLikedPosts(User principalUser, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<StudyGroup> likedStudyGroupList =
                studyGroupRepository.findByUserAndIsLiked(principalUser, pageable);

        return likedStudyGroupList.map(StudyGroupRes::from);
    }

    // 댓글단 게시글 조회
    public Page<GetCommentedStudyGroupRes> getCommentedPosts(User principalUser, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<StudyGroupComment> studyGroupComments =
                studyGroupCommentRepository.findByUser(principalUser, pageable);

        return studyGroupComments.map(GetCommentedStudyGroupRes::from);
    }

    // 스터디 그룹에 신청한 신청자 목록 조회
    public Page<StudyGroupApplyMemberRes> getAppliedStudyGroup(Long studyGroupId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(
                pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        Page<StudyGroupMember> member =
                studyGroupMemberRepository.findByStudyGroup(studyGroupId, pageable);

        return member.map(StudyGroupApplyMemberRes::from);
    }
}
