package com.example.weterview.dto.common.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public abstract class KakaoLoginRes {
    protected final boolean ourMember;

    protected KakaoLoginRes(boolean ourMember) {
        this.ourMember = ourMember;
    }
}
