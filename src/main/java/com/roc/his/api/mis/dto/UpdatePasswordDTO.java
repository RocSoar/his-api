package com.roc.his.api.mis.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class UpdatePasswordDTO {
    @NotBlank(message = "password不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{6,20}$", message = "password内容格式不正确")
    String password;

    @NotBlank(message = "newPassword")
    @Pattern(regexp = "^[a-zA-Z0-9]{6,20}$", message = "newPassword内容格式不正确")
    private String newPassword;
}
