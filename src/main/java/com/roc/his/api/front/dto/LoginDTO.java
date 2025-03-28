package com.roc.his.api.front.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class LoginDTO {
    @NotBlank(message = "tel不能为空")
    @Pattern(regexp = "^1[1-9]\\d{9}$", message = "手机号格式错误")
    private String tel;

    @NotBlank(message = "code不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "code内容错误")
    private String code;
}

