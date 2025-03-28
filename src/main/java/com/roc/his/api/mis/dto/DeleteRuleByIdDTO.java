package com.roc.his.api.mis.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class DeleteRuleByIdDTO {
    @NotNull(message = "id不能为空")
    @Min(value = 1, message = "userId不能小于1")
    private Integer id;
}

