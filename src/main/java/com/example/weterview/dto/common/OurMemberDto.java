package com.example.weterview.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OurMemberDto {
    @JsonProperty("isOurMember")
    private boolean isOurMember;
}
