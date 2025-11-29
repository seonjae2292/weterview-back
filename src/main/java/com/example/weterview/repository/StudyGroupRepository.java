package com.example.weterview.repository;

import com.example.weterview.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long>,
        JpaSpecificationExecutor<StudyGroup> {
    Page<StudyGroup> findByUser(User user, Pageable pageable);
    // Users 엔티티에서 kakaonum으로 user id 조회
    // studygroupmembership에서 user id로 조회
    // 조회한 게시글 id로 studygroup에서 조회
    @Query("SELECT sg FROM StudyGroup sg WHERE sg.id IN (SELECT s.studyGroup.id FROM StudyMembership s WHERE s.user.id = (SELECT u.id FROM User u WHERE u.kakaoUserNumber = :kakaoNum))")
    Page<StudyGroup> findByKakaonum(@Param("kakaoNum") String kakaoNum, Pageable pageable);
}



