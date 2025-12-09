package com.example.weterview.service;

import com.example.weterview.dto.common.ApiResponse;
import com.example.weterview.dto.myPage.response.GetHostedStudyGroupRes;
import com.example.weterview.dto.studyGroup.request.*;
import com.example.weterview.dto.studyGroup.response.CommentRes;
import com.example.weterview.dto.studyGroup.response.StudyGroupDetailRes;
import com.example.weterview.dto.studyGroup.response.StudyGroupRes;
import com.example.weterview.entity.*;
import com.example.weterview.entity.StudyGroupMember;
import com.example.weterview.enums.ErrorCode;
import com.example.weterview.enums.studyGroup.StatusEnum;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyGroupService {
    private final StudyGroupRepository studyGroupRepository;
    private final StudyMembershipRepository studyMembershipRepository;
    private final UserRepository userRepository;
    private final StudyGroupCommentRepository studyGroupCommentRepository;
    private final StudyGroupLikeRepository studyGroupLikeRepository;
    private final StudyGroupMemberRepository studyGroupMemberRepository;

    private final JwtUtil jwtUtil;

    // 스터디 그룹 게시글 생성
    @Transactional
    public void createStudyGroup(User principalUser, CreateStudyGroupReq req) {
        StudyGroup studyGroup = StudyGroup.create(principalUser, req);
        studyGroupRepository.save(studyGroup);

        StudyMembership studyMembership = StudyMembership.create(principalUser, studyGroup);
        studyMembershipRepository.save(studyMembership);
    }

    // [검색] 스터디 그룹 조회
    public Page<StudyGroupRes> getStudyGroup(GetStudyGroupReq req) {
        Pageable pageable = createPageable(req); // 페이지 조건
        Specification<StudyGroup> spec = buildSpecification(req); // 검색 조건

        Page<StudyGroup> studyGroupPage = studyGroupRepository.findAll(spec, pageable);

        return studyGroupPage.map(StudyGroupRes::from);
    }

    // 스터디 그룹 모집 게시글 단건 조회
    public GetStudyGroupByIdRes getStudyGroupById(Long id, User user) {
        StudyGroup studyGroup = studyGroupRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_NOT_FOUND));

        boolean isLiked = (user != null) &&
                studyGroupLikeRepository.existsByStudyGroupAndUserAndIsLiked(studyGroup, user, true);

        return GetStudyGroupByIdRes.from(studyGroup, isLiked);
    }

    // 스터디 그룹 수정
    public ApiResponse<?> updateStudyGroup(String id, UpdateStudyGroupReq req) {
        StudyGroup studyGroup = studyGroupRepository.findById(
                Long.parseLong(id))
                .orElseThrow(() -> new IllegalArgumentException("없는 게시글 입니다"));

        if (req.getField() != null) {
            studyGroup.setField(req.getField());
        }
        if (req.getStatus() != null) {
            studyGroup.setStatus(StatusEnum.valueOf(req.getStatus()));
        }
        if (req.getTitle() != null) {
            studyGroup.setTitle(req.getTitle());
        }
        if (req.getSubTitle() != null) {
            studyGroup.setSubTitle(req.getSubTitle());
        }
        if (req.getRecruitingNumber() != null) {
            studyGroup.setRecruitingNumber(req.getRecruitingNumber());
        }
        if (req.getTotalNumber() != null) {
            studyGroup.setTotalNumber(req.getTotalNumber());
        }
        if (req.getLocation() != null) {
            studyGroup.setLocation(req.getLocation());
        }
        if (req.getDescription() != null) {
            studyGroup.setDescription(req.getDescription());
        }
        if (req.getSchedule() != null) {
            studyGroup.setSchedule(req.getSchedule());
        }
        if (req.getJoinCondition() != null) {
            studyGroup.setJoinCondition(req.getJoinCondition());
        }
        if (req.getContact() != null) {
            studyGroup.setContact(req.getContact());
        }

        if (req.getStartDate() != null && req.getEndDate() != null) {
            LocalDateTime startDate = LocalDateTime.parse(req.getStartDate());
            LocalDateTime endDate = LocalDateTime.parse(req.getEndDate());
            if (startDate.isAfter(endDate)) {
                throw new IllegalArgumentException("시작일은 마감일 이전이어야 합니다.");
            }
            studyGroup.setStartDate(startDate);
            studyGroup.setEndDate(endDate);
        }

        studyGroupRepository.save(studyGroup);

        return ApiResponse.ok(studyGroup, "수정 성공");
    }

    // 스터디 그룹 삭제(soft)
    @Transactional
    public ApiResponse<?> deleteStudyGroup(String id) {
        StudyGroup studyGroup = studyGroupRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new IllegalArgumentException("없는 게시글 입니다."));

        if (studyGroup.getDeletedAt() != null) {
            return ApiResponse.BAD_REQUEST(null, "이미 삭제된 게시글 입니다.");
        }

        studyGroup.setDeletedAt(LocalDateTime.now());
        studyGroup.setStatus(StatusEnum.DELETED);

        return ApiResponse.ok(null, "삭제 성공");
    }

    public ApiResponse<?> createComment(CreateCommentReq req, String jwt) {
        StudyGroup studyGroup = studyGroupRepository.findById(Long.parseLong(req.getStudyGroupId()))
                .orElseThrow(() -> new IllegalArgumentException("없는 스터디 그룹 게시글 입니다."));

        String kakaoUserNumber = jwtUtil.getKakaoUserNumFromToken(jwt);
        User user = userRepository.findByKakaoUserNumber(kakaoUserNumber).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 입니다"));

        StudyGroupComment studyGroupComment = new StudyGroupComment();
        studyGroupComment.setContent(req.getContents());
        studyGroupComment.setStudyGroup(studyGroup);
        studyGroupComment.setUser(user);

        studyGroupCommentRepository.save(studyGroupComment);

        return ApiResponse.ok(null, "성공");
    }

    // 댓글 조회
    public ApiResponse<List<CommentRes>> getComment(String studyGroupId) {
        List<StudyGroupComment> comments = studyGroupCommentRepository.findByStudyGroupId(Long.parseLong(studyGroupId));

        List<CommentRes> result = List.of();

        if (!comments.isEmpty()) {
            result = comments.stream()
                    .map(item ->
                            new CommentRes(item.getContent(), item.getCreatedAt(), item.getUser().getNickname()))
                    .toList();
        }

        return ApiResponse.ok(result, "댓글 조회 성공");
    }

    // 게시글 좋아요
    public ApiResponse<?> likeStudyGroup(String studyGroupId, String jwt) {
        String kakaoUserNumber = jwtUtil.getKakaoUserNumFromToken(jwt);

        User user = userRepository.findByKakaoUserNumber(kakaoUserNumber)
                .orElseThrow(() -> new IllegalArgumentException("없는 회원정보 입니다."));
        StudyGroup studyGroup = studyGroupRepository.findById(Long.parseLong(studyGroupId))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글 입니다."));

        Optional<StudyGroupLike> studyGroupLike = studyGroupLikeRepository.findByStudyGroupAndUser(studyGroup, user);

        if (studyGroupLike.isPresent()) {
            studyGroupLike.get().setLiked(true);
            studyGroupLikeRepository.save(studyGroupLike.get());
        } else {
            StudyGroupLike like = new StudyGroupLike();
            like.setUser(user);
            like.setStudyGroup(studyGroup);
            like.setLiked(true);
            studyGroupLikeRepository.save(like);
        }

        return ApiResponse.ok(null, "좋아요 성공");
    }

    // 게시글 좋아요 취소
    public ApiResponse<?> unlikeStudyGroup(String studyGroupId, String jwt) {
        String kakaoUserNumber = jwtUtil.getKakaoUserNumFromToken(jwt);

        User user = userRepository.findByKakaoUserNumber(kakaoUserNumber)
                .orElseThrow(() -> new IllegalArgumentException("없는 회원정보 입니다."));
        StudyGroup studyGroup = studyGroupRepository.findById(Long.parseLong(studyGroupId))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글 입니다."));

        StudyGroupLike studyGroupLike = studyGroupLikeRepository.findByStudyGroupAndUser(studyGroup, user)
                .orElseThrow(() -> new IllegalArgumentException("없는 게시글입니다."));

        studyGroupLike.setUpdatedAt(LocalDateTime.now());
        studyGroupLike.setLiked(false);
        studyGroupLikeRepository.save(studyGroupLike);

        return ApiResponse.ok(null, "좋이요 취소 성공");
    }

    public ApiResponse<?> joinStudyGroup(String studyGroupId, String jwt) {
        StudyGroup studyGroup = studyGroupRepository.findById(Long.parseLong(studyGroupId)).orElseThrow(() ->
                new IllegalArgumentException("없는 스터디 그룹 게시글 입니다."));

        String kakaoUserNumber  = jwtUtil.getKakaoUserNumFromToken(jwt);
        User user = userRepository.findByKakaoUserNumber(kakaoUserNumber).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 사용자 입니다."));

        // 이미 신청한 사용자 인지 검증
        Optional<StudyGroupMember> studyGroupMember = studyGroupMemberRepository.findStudyGroupMemberByUser(user);
        if (studyGroupMember.isPresent()) {
            return ApiResponse.ok(null, "이미 신청한 사용자입니다.");
        }

        StudyGroupMember studyGroupMemberObj = new StudyGroupMember();
        studyGroupMemberObj.setUser(user);
        studyGroupMemberObj.setStudyGroup(studyGroup);
        studyGroupMemberObj.setJoin(JoinEnum.APPLY);

        studyGroupMemberRepository.save(studyGroupMemberObj);

        return ApiResponse.ok(null, "성공");
    }

    private Specification<StudyGroup> buildSpecification(GetStudyGroupReq req) {
        return Stream.of(
                        Optional.ofNullable(req.getField())
                                .map(f -> (Specification<StudyGroup>)((root, q, cb) -> cb.equal(root.get("field"), f))),
                        Optional.ofNullable(req.getStatus())
                                .map(s -> (Specification<StudyGroup>)((root, q, cb) -> cb.equal(root.get("status"), s))),
                        Optional.ofNullable(req.getLocation())
                                .map(l -> (Specification<StudyGroup>)((root, q, cb) -> cb.equal(root.get("location"), l))),
                        Optional.ofNullable(req.getTitle())
                                .filter(t -> !t.isBlank())
                                .map(t -> (Specification<StudyGroup>)((root, q, cb) ->
                                        cb.like(root.get("title"), "%" + t.trim() + "%")))
                )
                .flatMap(Optional::stream)
                .reduce(Specification::and)
                .orElse((root, q, cb) -> cb.conjunction());
    }

    public ApiResponse<StudyGroupDetailRes> getStudyGroupDetail(String studyGroupId){
        StudyGroup studyGroup = studyGroupRepository.findById(Long.parseLong(studyGroupId))
                .orElseThrow(() -> new IllegalArgumentException("해당하는 스터디 그룹이 존재하지 않습니다."));

        StudyGroupDetailRes result = new StudyGroupDetailRes();
        result.setField(studyGroup.getField());
        result.setStatus(studyGroup.getStatus());
        result.setRecruitingNumber(studyGroup.getRecruitingNumber());
        result.setTotalNumber(studyGroup.getTotalNumber());
        result.setStartDate(studyGroup.getStartDate());
        result.setEndDate(studyGroup.getEndDate());
        result.setLocation(studyGroup.getLocation());
        result.setTitle(studyGroup.getTitle());
        result.setSubTitle(studyGroup.getSubTitle());
        result.setDescription(studyGroup.getDescription());
        result.setSchedule(studyGroup.getSchedule());
        result.setJoinCondition(studyGroup.getJoinCondition());
        result.setContact(studyGroup.getContact());
        result.setCreatedAt(studyGroup.getCreatedAt());
        result.setUpdatedAt(studyGroup.getUpdatedAt());
        result.setDeletedAt(studyGroup.getDeletedAt());

        return ApiResponse.ok(result, "Success");
    }

    public Pageable createPageable(GetStudyGroupReq req) {
        return PageRequest.of(
                req.getPageNumber() - 1,
                req.getPageSize(),
                Sort.by("id").ascending()
        );
    }

    // 인기있는 스터디 그룹 조회
    public ApiResponse<Page<StudyGroupRes>> getPopularStudyGroup(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<StudyGroup> popularStudyGroup =
                studyGroupRepository.findPopularByApplicationCount(StatusEnum.RECRUITING, pageable);

        Page<StudyGroupRes> result = popularStudyGroup.map(StudyGroupRes::from);
        return ApiResponse.ok(result, "인기있는 스터디 그룹 게시글 조회 성공");
    }

    // 최신 스터디 그룹 조회
    public ApiResponse<List<StudyGroupRes>> getLatestStudyGroup(int count) {
        Pageable pageable = PageRequest.of(0, count);

        List<StudyGroup> latestStudyGroup =
                studyGroupRepository.findLatestByStatus(StatusEnum.RECRUITING, pageable);

        List<StudyGroupRes> result = latestStudyGroup.stream()
                .map(StudyGroupRes::from)
                .toList();

        return ApiResponse.ok(result, "최신 스터디 그룹 게시글 조회 성공");
    }
}
