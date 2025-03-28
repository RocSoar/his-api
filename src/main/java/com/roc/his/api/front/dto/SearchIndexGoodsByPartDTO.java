package com.roc.his.api.front.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class SearchIndexGoodsByPartDTO {
    @NotEmpty(message = "partIds不能为空")
    private Integer[] partIds;
}
