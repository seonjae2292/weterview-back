package com.example.weterview.repository;

import com.example.weterview.entity.StudyGroup;
import com.example.weterview.entity.StudyGroupComment;
import com.example.weterview.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyGroupCommentRepository extends JpaRepository<StudyGroupComment,Long> {
    List<StudyGroupComment> findByStudyGroupId(Long studyGroup_id);

    @Query(value = "select sgc " +
            "from StudyGroupComment sgc " +
            "join fetch sgc.studyGroup " +
            "where sgc.user = :user",
            countQuery = "select count(sgc) from StudyGroupComment sgc where sgc.user = :user")
    Page<StudyGroupComment> findByUser(@Param("user") User user, Pageable pageable);
}
