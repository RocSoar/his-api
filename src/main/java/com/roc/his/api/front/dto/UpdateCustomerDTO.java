package com.roc.his.api.front.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class UpdateCustomerDTO {
    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{2,10}$", message = "name内容不正确")
    private String name;

    @Pattern(regexp = "^男$|^女$", message = "sex内容不正确")
    private String sex;

    @NotBlank(message = "tel不能为空")
    @Pattern(regexp = "^1[1-9]\\d{9}$", message = "tel内容错误")
    private String tel;
}

