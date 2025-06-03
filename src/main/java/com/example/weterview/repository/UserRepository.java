package com.example.weterview.repository;

import com.example.weterview.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKakaoUserNumber(String kakaoUserNumber);
    boolean existsByKakaoUserNumber(String kakaoUserNumber);
    boolean existsByNickname(String nickname);
}