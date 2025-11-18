package com.example.weterview.dto.myPage.request;

import com.example.weterview.dto.common.response.PagiBasicDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class GetHostedStudyGroupReq extends PagiBasicDto {
    private String title;
}
