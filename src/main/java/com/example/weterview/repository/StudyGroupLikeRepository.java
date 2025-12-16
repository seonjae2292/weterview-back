package com.example.weterview.repository;

import com.example.weterview.entity.studyGroup.StudyGroup;
import com.example.weterview.entity.StudyGroupLike;
import com.example.weterview.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudyGroupLikeRepository extends JpaRepository<StudyGroupLike, Long> {
    Optional<StudyGroupLike> findByStudyGroupAndUser(StudyGroup studyGroup, User user);

    @Query(value = "select sgl " +
            "from StudyGroupLike sgl " +
            "join fetch sgl.studyGroup " +
            "where sgl.user = :user",
            countQuery = "select sgl from StudyGroupLike sgl where sgl.user = :user")
    Page<StudyGroupLike> findByUserAndIsLiked(@Param("user") User user, boolean isLiked, Pageable pageable);

    boolean existsByStudyGroupAndUserAndIsLiked(StudyGroup studyGroup, User user, boolean isLiked);
}
