package com.example.weterview.repository;

import com.example.weterview.dto.myPage.response.GetLikedPostRes;
import com.example.weterview.entity.StudyGroup;
import com.example.weterview.entity.StudyGroupLike;
import com.example.weterview.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudyGroupLikeRepository extends JpaRepository<StudyGroupLike, Long> {
    Optional<StudyGroupLike> findByStudyGroupAndUser(StudyGroup studyGroup, User user);

    Page<StudyGroupLike> findStudyGroupLikesByUser(User user, Pageable pageable);
}
