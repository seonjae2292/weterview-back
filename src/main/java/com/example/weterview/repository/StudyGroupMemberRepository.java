package com.example.weterview.repository;

import com.example.weterview.entity.studyGroup.StudyGroup;
import com.example.weterview.entity.StudyGroupMember;
import com.example.weterview.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudyGroupMemberRepository extends JpaRepository<StudyGroupMember, Long> {
    @Query(value = "select sgm " +
            "from StudyGroupMember sgm " +
            "join fetch User u " +
            "where sgm.studyGroup.id = :studyGroupId",
    countQuery = "select count(*) from StudyGroupMember sgm where sgm.studyGroup.id = :studyGroupId")
    Page<StudyGroupMember> findByStudyGroup(@Param("studyGroupId") Long studyGroupId, Pageable pageable);

    Optional<StudyGroupMember> findByUserIdAndStudyGroupId(Long userId, StudyGroup studyGroup);

}
