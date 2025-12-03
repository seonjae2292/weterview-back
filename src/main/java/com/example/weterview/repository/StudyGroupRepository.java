package com.example.weterview.repository;

import com.example.weterview.entity.*;
import com.example.weterview.enums.studyGroup.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long>,
        JpaSpecificationExecutor<StudyGroup> {
    Page<StudyGroup> findByUser(User user, Pageable pageable);
    // Users 엔티티에서 kakaonum으로 user id 조회
    // studygroupmembership에서 user id로 조회
    // 조회한 게시글 id로 studygroup에서 조회

    // 인기 스터디 게시글 조회
    @Query("SELECT sg FROM StudyGroup sg WHERE sg.id IN (SELECT s.studyGroup.id FROM StudyMembership s WHERE s.user.id = (SELECT u.id FROM User u WHERE u.kakaoUserNumber = :kakaoNum))")
    Page<StudyGroup> findByKakaonum(@Param("kakaoNum") String kakaoNum, Pageable pageable);

    @Query("SELECT s FROM StudyGroup s " +
            "LEFT JOIN StudyGroupMember m ON s.id = m.studyGroup.id " +
            "WHERE s.status = :status " +
            "GROUP BY s " +
            "ORDER BY COUNT(m) DESC, s.createdAt DESC")
    Page<StudyGroup> findPopularByApplicationCount(@Param("status") StatusEnum status, Pageable pageable);

    @Query("select sg from StudyGroup sg where sg.status = :status order by sg.createdAt DESC")
    List<StudyGroup> findLatestByStatus(@Param("status") StatusEnum status, Pageable pageable);
}

