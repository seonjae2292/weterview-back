package com.example.weterview.service;

import com.example.weterview.dto.common.ApiResponse;
import com.example.weterview.dto.studyGroup.request.CreateStudyGroupReq;
import com.example.weterview.entity.StudyGroup;
import com.example.weterview.repository.StudyGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudyGroupService {
    private final StudyGroupRepository studyGroupRepository;

    // 스터디 그룹 모임 생성
    public ApiResponse<?> createStudyGroup(CreateStudyGroupReq req) {
        try{
            StudyGroup studyGroup = new StudyGroup();

            studyGroup.setCategory(req.getCategory());
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
}
