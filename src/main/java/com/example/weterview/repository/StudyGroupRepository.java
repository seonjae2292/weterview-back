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

    @Query("SELECT s FROM StudyGroup s " +
            "LEFT JOIN StudyGroupMember m ON s.id = m.studyGroup.id " +
            "WHERE s.status = :status " +
            "GROUP BY s " +
            "ORDER BY COUNT(m) DESC, s.createdAt DESC")
    Page<StudyGroup> findPopularByApplicationCount(@Param("status") StatusEnum status, Pageable pageable);

    @Query("select sg from StudyGroup sg where sg.status = :status order by sg.createdAt DESC")
    List<StudyGroup> findLatestByStatus(@Param("status") StatusEnum status, Pageable pageable);
}

