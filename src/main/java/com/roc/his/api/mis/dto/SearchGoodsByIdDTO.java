package com.roc.his.api.mis.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public record SearchGoodsByIdDTO(
        @NotNull(message = "id不能为空")
        @Min(value = 1, message = "id不能小于1")
        Integer id
) {
}
