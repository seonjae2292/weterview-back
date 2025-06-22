package com.example.weterview.repository;

import com.example.weterview.entity.StudyGroup;
import com.example.weterview.entity.StudyGroupComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyGroupCommentRepository extends JpaRepository<StudyGroupComment,Long> {
    List<StudyGroupComment> findByStudyGroupId(Long studyGroup_id);
}
