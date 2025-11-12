package com.example.weterview.repository;

import com.example.weterview.entity.StudyGroup;
import com.example.weterview.entity.StudyMembership;
import com.example.weterview.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyMembershipRepository extends JpaRepository<StudyMembership, Long> {
    Optional<StudyMembership> findById(Long studyGroupId);
    Optional<StudyMembership> findByStudyGroupIdAndUserId(StudyGroup studyGroupId, User userId);

    @Query(value = "select User " +
            "from User u " +
            "where u.id in (select m.user from StudyMembership m where m.studyGroup = : studyGroupId)", nativeQuery = true)
    Optional<List<User>> findByStudyGroupId(long studyGroupId);

    @Query("select m.studyGroup from StudyMembership m where m.user = : user")
    Page<StudyGroup> findByUserWithStudyGroup(@Param("user") User user, Pageable pageable);
}
