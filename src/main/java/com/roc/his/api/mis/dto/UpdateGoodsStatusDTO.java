package com.roc.his.api.mis.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class UpdateGoodsStatusDTO {
    @NotNull(message = "id不能为空")
    @Positive(message = "id不能小于1")
    private Integer id;

    @NotNull(message = "status不能为空")
    private Boolean status;
}

