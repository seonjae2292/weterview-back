package com.example.weterview.repository;

import com.example.weterview.entity.StudyGroup;
import com.example.weterview.entity.StudyGroupLike;
import com.example.weterview.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudyGroupLikeRepository extends JpaRepository<StudyGroupLike, Long> {
    Optional<StudyGroupLike> findByStudyGroupAndUser(StudyGroup studyGroup, User user);
}
