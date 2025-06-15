package com.example.weterview.controller;

import com.example.weterview.dto.common.ApiResponse;
import com.example.weterview.dto.studyGroup.request.CreateStudyGroupReq;
import com.example.weterview.dto.studyGroup.request.GetStudyGroupReq;
import com.example.weterview.dto.studyGroup.request.UpdateStudyGroupReq;
import com.example.weterview.service.StudyGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/studygroup")
@RequiredArgsConstructor
public class StudyGroupController {
    private final StudyGroupService studyGroupService;

    @PostMapping("/create")
    public ApiResponse<?> createStudyGroup(@RequestBody CreateStudyGroupReq req) {
        return studyGroupService.createStudyGroup(req);
    }

    @GetMapping("/get")
    public ApiResponse<?> getStudyGroup(@ModelAttribute @Valid GetStudyGroupReq req)  {
        return studyGroupService.getStudyGroup(req);
    }

    @PatchMapping("/update/{id}")
    public ApiResponse<?> updateStudyGroup(
            @PathVariable String id,
            @RequestBody @Valid UpdateStudyGroupReq req) {
        return studyGroupService.updateStudyGroup(id, req);
    }
}
