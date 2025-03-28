package com.roc.his.api.front.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class CreatePaymentDTO {
    @NotNull(message = "goodsId不能为空")
    @Min(value = 1, message = "goodsId不能小于1")
    private Integer goodsId;

    @NotNull(message = "number不能为空")
    @Min(value = 1, message = "number不能小于1")
    private Integer number;
}

