package com.example.weterview.service;

import com.example.weterview.dto.common.ApiResponse;
import com.example.weterview.entity.StudyGroup;
import com.example.weterview.entity.StudyGroupLike;
import com.example.weterview.entity.User;
import com.example.weterview.enums.ErrorCode;
import com.example.weterview.exception.CustomException;
import com.example.weterview.repository.StudyGroupLikeRepository;
import com.example.weterview.repository.StudyGroupRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class StudyGroupLikeService {
    private final StudyGroupRepository studyGroupRepository;
    private final StudyGroupLikeRepository studyGroupLikeRepository;

    // 게시글 좋아요
    @Transactional
    public void likeStudyGroup(User principalUser, String studyGroupId) {
        StudyGroup studyGroup = studyGroupRepository.findById(Long.parseLong(studyGroupId))
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_GROUP_NOT_FOUND));

        Optional<StudyGroupLike> studyGroupLike =
                studyGroupLikeRepository.findByStudyGroupAndUser(studyGroup, principalUser);

        if (studyGroupLike.isPresent()) {
            studyGroupLike.get().toggle();
        } else {
            studyGroupLikeRepository.save(StudyGroupLike.create(principalUser, studyGroup));
        }
    }
}
