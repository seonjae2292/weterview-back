package com.example.weterview.controller;

import com.example.weterview.dto.common.ApiResponse;
import com.example.weterview.dto.studyGroup.request.*;
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
    public ApiResponse<?> getStudyGroup(@ModelAttribute @Valid GetStudyGroupReq req) {
        return studyGroupService.getStudyGroup(req);
    }

    @GetMapping("/get/{id}")
    public ApiResponse<GetStudyGroupByIdRes> getStudyGroupById(@PathVariable String id) {
        return studyGroupService.getStudyGroupById(id);
    }

    @PatchMapping("/update/{id}")
    public ApiResponse<?> updateStudyGroup(
            @PathVariable String id,
            @RequestBody @Valid UpdateStudyGroupReq req) {
        return studyGroupService.updateStudyGroup(id, req);
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<?> deleteStudyGroup(@PathVariable String id) {
        return studyGroupService.deleteStudyGroup(id);
    }

    @PostMapping("/join")
    public ApiResponse<?> joinStudyGroup(
            @RequestBody JoinStudyGroupReq req,
            @RequestHeader("Authorization") String jwt) {
        return studyGroupService.joinStudyGroup(req, jwt);
    }
}
