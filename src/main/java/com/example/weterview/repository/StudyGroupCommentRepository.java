package com.example.weterview.repository;

import com.example.weterview.entity.StudyGroup;
import com.example.weterview.entity.StudyGroupComment;
import com.example.weterview.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyGroupCommentRepository extends JpaRepository<StudyGroupComment,Long> {
    List<StudyGroupComment> findByStudyGroupId(Long studyGroup_id);

    Page<StudyGroupComment> findStudyGroupCommentsByUser(User user, Pageable pageable);
}
