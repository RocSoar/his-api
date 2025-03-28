package com.roc.his.api.front.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;


public record SearchGoodsByIdDTO(
        @NotNull(message = "id不能为空")
        @Positive(message = "id不能小于1")
        Integer id
) {
}
