package com.example.weterview.repository;

import com.example.weterview.entity.*;
import com.example.weterview.entity.studyGroup.StudyGroup;
import com.example.weterview.enums.studyGroup.StatusEnum;
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

    @Query("SELECT s FROM StudyGroup s " +
            "LEFT JOIN StudyGroupMember m ON s.id = m.studyGroup.id " +
            "WHERE s.status = :status " +
            "GROUP BY s " +
            "ORDER BY COUNT(m) DESC, s.createdAt DESC")
    Page<StudyGroup> findPopularByApplicationCount(@Param("status") StatusEnum status, Pageable pageable);

    @Query("select sg from StudyGroup sg where sg.status = :status order by sg.createdAt DESC")
    List<StudyGroup> findLatestByStatus(@Param("status") StatusEnum status, Pageable pageable);

    // 내가 좋아요한 게시글 목록
    @Query(value = "select sgl.studyGroup from StudyGroupLike sgl " +
            "join sgl.studyGroup " +
            "where sgl.user = :user and sgl.isLiked = true",
            countQuery = "select count(sgl) from StudyGroupLike sgl " +
                    "where sgl.user = :user and sgl.isLiked = true")
    Page<StudyGroup> findByUserAndIsLiked(@Param("user") User user, Pageable pageable);
}

