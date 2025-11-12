package com.example.weterview.service;

import com.example.weterview.dto.common.ApiResponse;
import com.example.weterview.dto.studyGroup.request.*;
import com.example.weterview.dto.studyGroup.response.GetCommentRes;
import com.example.weterview.dto.studyGroup.response.GetStudyGroupDetailRes;
import com.example.weterview.dto.studyGroup.response.GetStudyGroupPageRes;
import com.example.weterview.entity.*;
import com.example.weterview.enums.studyGroup.StatusEnum;
import com.example.weterview.repository.*;
import com.example.weterview.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
public class StudyGroupService {
    private final StudyGroupRepository studyGroupRepository;
    private final StudyMembershipRepository studyMembershipRepository;
    private final UserRepository userRepository;
    private final StudyGroupCommentRepository studyGroupCommentRepository;
    private final StudyGroupLikeRepository studyGroupLikeRepository;

    private final JwtUtil jwtUtil;

    // 스터디 그룹 모임 생성
    public ApiResponse<?> createStudyGroup(String jwt, CreateStudyGroupReq req) {
        try{
            // 토큰에서 username 추출
            String name = jwtUtil.getUsernameFromToken(jwt);

            // username으로 User 테이블에서 userId 조회
            User user = userRepository.findByName(name)
                    .orElseThrow(() -> new IllegalArgumentException("username에 해당하는 사용자가 존재하지 않습니다."));

            StudyGroup studyGroup = new StudyGroup();

            studyGroup.setUser(user);
            studyGroup.setField(req.getField());
            studyGroup.setTitle(req.getTitle());
            studyGroup.setSubTitle(req.getSubTitle());
            studyGroup.setRecruitingNumber(req.getRecruitingNumber());
            studyGroup.setTotalNumber(req.getTotalNumber());
            studyGroup.setStartDate(LocalDateTime.parse(req.getStartDate()));
            studyGroup.setEndDate(LocalDateTime.parse(req.getEndDate()));
            studyGroup.setLocation(req.getLocation());
            studyGroup.setDescription(req.getDescription());
            studyGroup.setSchedule(req.getSchedule());
            studyGroup.setJoinCondition(req.getJoinCondition());
            studyGroup.setContact(req.getContact());

            studyGroupRepository.save(studyGroup);

            return ApiResponse.ok(null, "스터디 그룹을 생성했습니다");
        } catch (Exception e) {
            log.info(e.getMessage());
            return ApiResponse.BAD_REQUEST(null, "스터디 그룹 생성중 오류가 발생했습니다");
        }
    }

    // 스터디 그룹 조회
    public ApiResponse<?> getStudyGroup(GetStudyGroupReq req) {
        Pageable pageable = createPageable(req); // 페이지 조건
        Specification<StudyGroup> spec = buildSpecification(req); // 검색 조건
        Page<StudyGroup> entityPage = studyGroupRepository.findAll(spec, pageable);

        // StudyGroup Entity를 GetStudyGroupPageRes DTO로 변환하는 함수
        Page<GetStudyGroupPageRes> paged = entityPage.map(e -> {
            GetStudyGroupPageRes dto = new GetStudyGroupPageRes();
            // 리플렉션을 사용하여 source 객체의 property를 target 객체로 복사
            BeanUtils.copyProperties(e, dto);
            return dto;
        });

        return ApiResponse.ok(paged, "스터디그룹 목록 조회 성공");
    }

    // 스터디 그룹 단일 조회
    public ApiResponse<GetStudyGroupByIdRes> getStudyGroupById(String id) {
        StudyGroup studyGroup = studyGroupRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new IllegalArgumentException("없는 게시글 입니다."));

        GetStudyGroupByIdRes getStudyGroupByIdRes = GetStudyGroupByIdRes.builder()
                .id(studyGroup.getId().toString())
                .field(studyGroup.getField())
                .status(studyGroup.getStatus())
                .title(studyGroup.getTitle())
                .subTitle(studyGroup.getSubTitle())
                .recruitingNumber(studyGroup.getRecruitingNumber())
                .totalNumber(studyGroup.getTotalNumber())
                .startDate(studyGroup.getStartDate())
                .endDate(studyGroup.getEndDate())
                .location(studyGroup.getLocation())
                .joinCondition(studyGroup.getJoinCondition())
                .contact(studyGroup.getContact())
                .createdAt(studyGroup.getCreatedAt())
                .updatedAt(studyGroup.getUpdatedAt())
                .build();

        return ApiResponse.ok(getStudyGroupByIdRes, "조회성공");
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

        String kakaoUserNumber = jwtUtil.getUsernameFromToken(jwt);
        User user = userRepository.findByKakaoUserNumber(kakaoUserNumber).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 입니다"));

        StudyGroupComment studyGroupComment = new StudyGroupComment();
        studyGroupComment.setContent(req.getContents());
        studyGroupComment.setStudyGroup(studyGroup);
        studyGroupComment.setUser(user);

        studyGroupCommentRepository.save(studyGroupComment);

        return ApiResponse.ok(null, "성공");
    }

    public ApiResponse<List<GetCommentRes>> getComment(String studyGroupId) {
        List<StudyGroupComment> comments = studyGroupCommentRepository.findByStudyGroupId(Long.parseLong(studyGroupId));

        if (comments.isEmpty()) {
            throw new IllegalArgumentException("댓글없음");
        }

        List<GetCommentRes> result = comments.stream()
                .map(item ->
                        new GetCommentRes(item.getContent(), item.getCreatedAt(), item.getUser().getNickname()))
                .toList();

        return ApiResponse.ok(result, "성공");
    }

    public ApiResponse<?> likeStudyGroup(String studyGroupId, String jwt) {
        String kakaoUserNumber = jwtUtil.getUsernameFromToken(jwt);

        User user = userRepository.findByKakaoUserNumber(kakaoUserNumber)
                .orElseThrow(() -> new IllegalArgumentException("없는 회원정보 입니다."));
        StudyGroup studyGroup = studyGroupRepository.findById(Long.parseLong(studyGroupId))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글 입니다."));

        StudyGroupLike studyGroupLike = new StudyGroupLike();
        studyGroupLike.setUser(user);
        studyGroupLike.setStudyGroup(studyGroup);

        studyGroupLikeRepository.save(studyGroupLike);

        return ApiResponse.ok(null, "좋아요 성공");
    }

    // Soft Delete로 구현
    public ApiResponse<?> unlikeStudyGroup(String studyGroupId, String jwt) {
        String kakaoUserNumber = jwtUtil.getUsernameFromToken(jwt);

        User user = userRepository.findByKakaoUserNumber(kakaoUserNumber)
                .orElseThrow(() -> new IllegalArgumentException("없는 회원정보 입니다."));
        StudyGroup studyGroup = studyGroupRepository.findById(Long.parseLong(studyGroupId))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글 입니다."));

        StudyGroupLike studyGroupLike = studyGroupLikeRepository.findByStudyGroupAndUser(studyGroup, user)
                .orElseThrow(() -> new IllegalArgumentException("없는 게시글입니다."));

        studyGroupLike.setDeletedAt(LocalDateTime.now());
        studyGroupLikeRepository.save(studyGroupLike);

        return ApiResponse.ok(null, "좋이요 취소 성공");
    }

    // 토큰 -> 토큰의 subject에서 kakaoUserNumber 추출 -> 존재하는 번호인지 확인 -> 성공
    public ApiResponse<?> joinStudyGroup(JoinStudyGroupReq req, String jwt) {
        StudyGroup studyGroup = studyGroupRepository.findById(Long.parseLong(req.getStudyGroupId())).orElseThrow(() ->
                new IllegalArgumentException("없는 스터디 그룹 게시글 입니다."));

        String kakaoUserNumber  = jwtUtil.getUsernameFromToken(jwt);
        User user = userRepository.findByKakaoUserNumber(kakaoUserNumber).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 사용자 입니다."));

        // 이미 신청한 사용자 인지 검증
        boolean isAlreadyMember = studyMembershipRepository.findByStudyGroupIdAndUserId(studyGroup, user).isPresent();
        if (isAlreadyMember) {
            return ApiResponse.BAD_REQUEST(null, "이미 신청한 사용자 입니다.");
        }

        StudyMembership studyMembership = new StudyMembership();
        studyMembership.setStudyGroup(studyGroup);
        studyMembership.setUser(user);

        studyMembershipRepository.save(studyMembership);

        return ApiResponse.ok(null, "성공");
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

    public ApiResponse<GetStudyGroupDetailRes> getStudyGroupDetail(String studyGroupId){
        StudyGroup studyGroup = studyGroupRepository.findById(Long.parseLong(studyGroupId))
                .orElseThrow(() -> new IllegalArgumentException("해당하는 스터디 그룹이 존재하지 않습니다."));

        GetStudyGroupDetailRes result = new GetStudyGroupDetailRes();
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

    /// 스터디 그룹 신청자 조회
    /// 스터디 그룹에 어떤 사용자들이 신청했는지 조회
    /// 스터디 그룹 상세 정보에서 자신이 개설한 정보 확인
    public ApiResponse<List<User>> getAppliedStudyGroup(String studyGroupId) {
        /// 2. 스터디 그룹 멤버십 테이블에서 정보 조회
        /// 특정 스터디 그룹에 속해 있는 사용자 목록 데이터
        List<User> userList =
                studyMembershipRepository
                        .findByStudyGroupId(Long.valueOf(studyGroupId))
                        .orElseThrow(() -> new IllegalArgumentException("해당 스터디 그룹에 속한 사용자가 없습니다."));

        return ApiResponse.ok(userList, "success");
    }

}
