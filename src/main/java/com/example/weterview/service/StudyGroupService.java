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
import java.time.format.DateTimeParseException;
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
        LocalDateTime start = parseDateTime(req.getStartDate());
        LocalDateTime end = parseDateTime(req.getEndDate());

        StudyGroup studyGroup = StudyGroup.builder()
                .user(principalUser)
                .field(req.getField())
                .title(req.getTitle())
                .subTitle(req.getSubTitle())
                .recruitingNumber(req.getRecruitingNumber())
                .totalNumber(req.getTotalNumber())
                .startDate(start)  // 파싱된 객체 전달
                .endDate(end)
                .location(req.getLocation())
                .description(req.getDescription())
                .schedule(req.getSchedule())
                .joinCondition(req.getJoinCondition())
                .contact(req.getContact())
                .build();

        studyGroupRepository.save(studyGroup);

        // 4. 멤버십 생성 (작성자를 관리자로 등록)
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
    @Transactional
    public void updateStudyGroup(Long id, UpdateStudyGroupReq req) {
        StudyGroup studyGroup = studyGroupRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_NOT_FOUND));

        // 1. 기본 정보 수정
        studyGroup.updateInfo(
                req.getTitle(),
                req.getSubTitle(),
                req.getField(),
                req.getLocation(),
                req.getDescription(),
                req.getJoinCondition(),
                req.getContact()
        );

        LocalDateTime start = parseDateTime(req.getStartDate());
        LocalDateTime end = parseDateTime(req.getEndDate());
        studyGroup.reschedule(req.getSchedule(), start, end);

        // 3. 인원 변경
        studyGroup.updateRecruitment(req.getRecruitingNumber(), req.getTotalNumber());

        // 4. 상태 변경
        studyGroup.changeStatus(req.getStatus());
    }

    // 스터디 그룹 삭제(soft)
    @Transactional
    public void deleteStudyGroup(Long id) {
        StudyGroup studyGroup = studyGroupRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_NOT_FOUND));

        if (studyGroup.getDeletedAt() != null) {
            throw new CustomException(ErrorCode.ALREADY_DELETE);
        }

        studyGroup.delete();
    }

    // 스터디 그룹 참여 신청
    public void joinStudyGroup(User principalUser, Long studyGroupId) {
        StudyGroup studyGroup = studyGroupRepository.findById(studyGroupId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_NOT_FOUND));

        StudyGroupMember studyGroupMember =
                studyGroupMemberRepository.findByUserIdAndStudyGroupId(principalUser.getId(), studyGroup)
                        .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_MEMBER_NOT_FOUND));

        studyGroupMember.applyJoin();
    }

    // 댓글 추가
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

    public Pageable createPageable(GetStudyGroupReq req) {
        return PageRequest.of(
                req.getPageNumber() - 1,
                req.getPageSize(),
                Sort.by("id").ascending()
        );
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

    private LocalDateTime parseDateTime(String dateTimeStr) {
        // null이거나 빈 문자열이면 null 반환 (날짜 수정 의도 없음)
        if (dateTimeStr == null || dateTimeStr.isBlank()) {
            return null;
        }
        try {
            // ISO-8601 형식 (yyyy-MM-ddTHH:mm:ss) 파싱
            return LocalDateTime.parse(dateTimeStr);
        } catch (DateTimeParseException e) {
            // 형식이 맞지 않으면 예외 발생 (400 Bad Request)
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }
}
