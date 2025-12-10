package com.example.weterview.service;

import com.example.weterview.dto.common.ApiResponse;
import com.example.weterview.entity.StudyGroup;
import com.example.weterview.entity.StudyGroupLike;
import com.example.weterview.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class StudyGroupLikeService {

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

}
