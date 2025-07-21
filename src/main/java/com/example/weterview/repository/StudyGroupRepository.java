package com.example.weterview.repository;

import com.example.weterview.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long>,
        JpaSpecificationExecutor<StudyGroup> {
    Page<StudyGroup> findByUser(User user, Pageable pageable);
}

