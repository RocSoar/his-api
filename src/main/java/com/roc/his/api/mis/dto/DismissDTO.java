package com.roc.his.api.mis.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class DismissDTO {
    @NotNull(message = "userId不能为空")
    @Min(value = 1, message = "userId不能小于1")
    private Integer userId;
}

