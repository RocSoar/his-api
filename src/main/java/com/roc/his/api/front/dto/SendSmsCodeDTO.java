package com.roc.his.api.front.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class SendSmsCodeDTO {
    @NotBlank(message = "tel不能为空")
    @Pattern(regexp = "^1[1-9]\\d{9}$", message = "手机号码格式不正确")
    private String tel;
}

