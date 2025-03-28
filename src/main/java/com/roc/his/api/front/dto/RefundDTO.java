package com.roc.his.api.front.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class RefundDTO {
    @NotNull
    @Min(value = 1, message = "id不能小于1")
    private Integer id;
}

