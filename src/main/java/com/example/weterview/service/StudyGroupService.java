package com.example.weterview.service;

import com.example.weterview.dto.common.ApiResponse;
import com.example.weterview.dto.studyGroup.request.CreateStudyGroupReq;
import com.example.weterview.dto.studyGroup.request.GetStudyGroupReq;
import com.example.weterview.dto.studyGroup.response.GetStudyGroupPageRes;
import com.example.weterview.entity.StudyGroup;
import com.example.weterview.repository.StudyGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudyGroupService {
    private final StudyGroupRepository studyGroupRepository;

    // 스터디 그룹 모임 생성
    public ApiResponse<?> createStudyGroup(CreateStudyGroupReq req) {
        try{
            StudyGroup studyGroup = new StudyGroup();

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

    public ApiResponse<?> getStudyGroup(GetStudyGroupReq req) {
        Pageable pageable = PageRequest.of(
                req.getPageNumber() - 1,
                req.getPageSize(),
                Sort.by("id").ascending()
        );

        Specification<StudyGroup> spec = buildSpecification(req);

        Page<StudyGroup> entityPage = studyGroupRepository.findAll(spec, pageable);

        Page<GetStudyGroupPageRes> paged = entityPage.map(e -> {
            GetStudyGroupPageRes dto = new GetStudyGroupPageRes();
            BeanUtils.copyProperties(e, dto);
            return dto;
        });

        return ApiResponse.ok(paged, "스터디그룹 목록 조회 성공");
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
}
