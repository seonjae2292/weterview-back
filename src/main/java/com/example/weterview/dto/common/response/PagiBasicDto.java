package com.example.weterview.dto.common.response;

import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PagiBasicDto {
    @Min(value = 1, message = "page는 1 이상이어야 합니다.")
    private int pageNumber = 1;
    @Min(value = 1, message = "size는 1 이상이어야 합니다.")
    private int pageSize = 10;
}
