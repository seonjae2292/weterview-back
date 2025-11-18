package com.example.weterview.dto.studyGroup.request;

import com.example.weterview.dto.common.response.PagiBasicDto;
import com.example.weterview.enums.studyGroup.FieldEnum;
import com.example.weterview.enums.studyGroup.LocationEnum;
import com.example.weterview.enums.studyGroup.StatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class GetStudyGroupReq extends PagiBasicDto {
    private String title;
    private FieldEnum field;
    private LocationEnum location;
    private StatusEnum status;
}
