package com.roc.his.api.mis.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class UploadCheckupExcelDTO {
    @NotNull(message = "id不能为null")
    @Positive(message = "id不能小于1")
    private Integer id;
}

