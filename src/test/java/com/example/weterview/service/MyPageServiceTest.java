package com.example.weterview.service;

import com.example.weterview.dto.myPage.response.MyPageRes;
import com.example.weterview.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

// ⭐️ 서비스 테스트의 필수품: Mockito 확장 기능 켜기
@ExtendWith(MockitoExtension.class)
class MyPageServiceTest {

    // ⭐️ 테스트할 대상(Service)에 가짜 객체들을 주입해 주는 역할
    // 지금은 의존성이 없지만, 나중에 Repository가 생기면 알아서 넣어줌!
    @InjectMocks
    private MyPageService myPageService;

    @Test
    @DisplayName("서비스: 유저 정보를 받으면 응답 DTO를 반환한다")
    void getMyPageInfo_Success() {
        // 1. Given (준비)
        // 우리가 아까 만든 Builder로 테스트용 유저를 만들어
        User user = User.builder()
                .id(1L)
                .nickname("자바꿈나무")
                .kakaoEmail("dream@java.com")
                .gender("male")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 2. When (실행)
        // 실제로 서비스 메서드를 호출해 봐
        MyPageRes response = myPageService.getMyPageInfo(user);

        // 3. Then (검증)
        // 결과가 null이 아니고, 내용이 맞는지 확인
        assertThat(response).isNotNull();
        assertThat(response.getNickname()).isEqualTo("자바꿈나무");
        assertThat(response.getKakaoEmail()).isEqualTo("dream@java.com");
    }
}