package com.example.weterview.service;

import com.example.weterview.dto.studyGroup.request.*;
import com.example.weterview.dto.common.response.StudyGroupRes;
import com.example.weterview.entity.*;
import com.example.weterview.entity.StudyGroupMember;
import com.example.weterview.entity.studyGroup.StudyGroup;
import com.example.weterview.entity.studyGroup.vo.StudyContent;
import com.example.weterview.enums.ResultCode;
import com.example.weterview.enums.studyGroup.LocationEnum;
import com.example.weterview.enums.studyGroup.StatusEnum;
import com.example.weterview.exception.CustomException;
import com.example.weterview.repository.*;
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
import java.util.Set;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyGroupService {
    private final StudyGroupRepository studyGroupRepository;
    private final StudyMembershipRepository studyMembershipRepository;
    private final StudyGroupLikeRepository studyGroupLikeRepository;
    private final StudyGroupMemberRepository studyGroupMemberRepository;

    // 스터디 그룹 게시글 생성
    @Transactional
    public void createStudyGroup(User principalUser, CreateStudyGroupReq req) {
        StudyGroup studyGroup = req.toEntity(principalUser);
        studyGroupRepository.save(studyGroup);

        // 멤버십 생성 (작성자를 관리자로 등록)
        StudyMembership studyMembership = StudyMembership.create(principalUser, studyGroup);
        studyMembershipRepository.save(studyMembership);
    }

    // [검색] 스터디 그룹 조회
    public Page<StudyGroupRes> getStudyGroup(User principalUser, GetStudyGroupReq req) {
        Pageable pageable = createPageable(req); // 페이지 조건
        Specification<StudyGroup> spec = buildSpecification(req); // 검색 조건

        Page<StudyGroup> studyGroupPage = studyGroupRepository.findAll(spec, pageable);

        // 비로그인
        if (principalUser == null) {
            return studyGroupPage.map(StudyGroupRes::from);
        }

        // 조회한 스터디 그룹 ID만 추출
        List<Long> studyIds = studyGroupPage.getContent().stream()
                .map(StudyGroup::getId)
                .toList();

        Set<Long> likedStudyIds = studyGroupLikeRepository.findLikedStudyIds(principalUser.getId(), studyIds);

        return studyGroupPage.map(studyGroup -> {
            boolean isLiked = likedStudyIds.contains(studyGroup.getId());
            return StudyGroupRes.of(studyGroup, isLiked);
        });
    }

    // 스터디 그룹 모집 게시글 단건 조회
    public StudyGroupRes getStudyGroupById(Long id, User principalUser) {
        StudyGroup studyGroup = studyGroupRepository.findById(id)
                .orElseThrow(() -> new CustomException(ResultCode.STUDY_GROUP_NOT_FOUND));

        if (principalUser == null) {
            return StudyGroupRes.from(studyGroup);
        }

        boolean isLiked = studyGroupLikeRepository
                .existsByStudyGroupAndUserAndIsLiked(studyGroup, principalUser, true);

        return StudyGroupRes.of(studyGroup, isLiked);
    }

    // 스터디 그룹 수정
    @Transactional
    public void updateStudyGroup(Long id, UpdateStudyGroupReq req) {
        StudyGroup studyGroup = studyGroupRepository.findById(id)
                .orElseThrow(() -> new CustomException(ResultCode.STUDY_GROUP_NOT_FOUND));

        StudyContent mergedContent = req.toContent(studyGroup.getContent());
        LocationEnum mergedLocation =
                req.getLocation() != null ? req.getLocation() : studyGroup.getLocation();

        // 기본 정보 수정
        studyGroup.updateInfo(mergedContent, mergedLocation);

        // 시간 수정
        if(studyGroup.getPeriod() != null){
            studyGroup.reschedule(req.toPeriod());
        }

        // 인원 변경
        if (req.getRecruitingNumber() != null) {
            studyGroup.updateRecruitment(req.getRecruitingNumber());
        }
    }

    // 스터디 그룹 삭제(soft)
    @Transactional
    public void deleteStudyGroup(Long id) {
        StudyGroup studyGroup = studyGroupRepository.findById(id)
                .orElseThrow(() -> new CustomException(ResultCode.STUDY_GROUP_NOT_FOUND));

        if (studyGroup.getDeletedAt() != null) {
            throw new CustomException(ResultCode.ALREADY_DELETED_STUDY_GROUP);
        }

        studyGroup.delete();
    }

    // 스터디 그룹 참여 신청
    public void joinStudyGroup(User principalUser, Long studyGroupId) {
        StudyGroup studyGroup = studyGroupRepository.findById(studyGroupId)
                .orElseThrow(() -> new CustomException(ResultCode.STUDY_GROUP_NOT_FOUND));

        StudyGroupMember studyGroupMember =
                studyGroupMemberRepository.findByUserIdAndStudyGroupId(principalUser.getId(), studyGroup)
                        .orElseThrow(() -> new CustomException(ResultCode.STUDY_GROUP_MEMBER_NOT_FOUND));

        studyGroupMember.applyJoin();
    }

    // 인기있는 스터디 그룹 조회
    public Page<StudyGroupRes> getPopularStudyGroup(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize,
                Sort.by(Sort.Direction.DESC));

        Page<StudyGroup> popularStudyGroup =
                studyGroupRepository.findPopularByApplicationCount(StatusEnum.RECRUITING, pageable);

        Page<StudyGroupRes> result = popularStudyGroup.map(StudyGroupRes::from);
        return result;
    }

    // 최신 스터디 그룹 조회
    public List<StudyGroupRes> getLatestStudyGroup(int count) {
        Pageable pageable = PageRequest.of(0, count);

        List<StudyGroup> latestStudyGroup =
                studyGroupRepository.findLatestByStatus(StatusEnum.RECRUITING, pageable);

        List<StudyGroupRes> result = latestStudyGroup.stream()
                .map(StudyGroupRes::from)
                .toList();

        return result;
    }

    // Page 객체 생성
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
            throw new CustomException(ResultCode.INVALID_INPUT_VALUE);
        }
    }
}
