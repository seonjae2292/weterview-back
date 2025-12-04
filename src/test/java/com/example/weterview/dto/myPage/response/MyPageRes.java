package com.example.weterview.dto.myPage.response;

import com.example.weterview.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class MyPageResponseTest {

    @Test
    @DisplayName("User 엔티티로 MyPageResponse 생성이 올바르게 된다")
    void createMyPageResponse() {
        // 1. Given (준비: 가짜 User 엔티티 만들기)
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .kakaoEmail("mentor@test.com")
                .nickname("자바멘토")
                .gender("male")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // 2. When (실행: 우리가 만든 변환 메서드 호출)
        MyPageRes response = MyPageRes.from(user);

        // 3. Then (검증: 값이 제대로 들어갔는지 확인)
        // AssertJ의 assertThat을 사용하면 가독성이 좋아
        assertThat(response.getKakaoEmail()).isEqualTo("mentor@test.com");
        assertThat(response.getNickname()).isEqualTo("자바멘토");
        assertThat(response.getGender()).isEqualTo("male");

        // 날짜 같은 건 완전히 똑같은지 체크
        assertThat(response.getCreatedAt()).isEqualTo(now);
    }
}