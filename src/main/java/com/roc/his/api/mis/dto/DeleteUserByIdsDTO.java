package com.roc.his.api.mis.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class DeleteUserByIdsDTO {
    @NotEmpty(message = "ids不能为空")
    private Integer[] ids;
}

