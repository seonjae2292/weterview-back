package com.example.weterview.controller;

import com.example.weterview.dto.common.ApiResponse;
import com.example.weterview.dto.studyGroup.request.CreateStudyGroupReq;
import com.example.weterview.service.StudyGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/studygroup")
@RequiredArgsConstructor
public class StudyGroupController {
    private final StudyGroupService studyGroupService;

    @PostMapping("/create")
    public ApiResponse<?> createStudyGroup(@RequestBody CreateStudyGroupReq req) {
        return studyGroupService.createStudyGroup(req);
    }
}
