package com.roc.his.api.mis.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class InsertDeptDTO {

    @NotBlank(message = "deptName不能为空")
    private String deptName;

    @Pattern(regexp = "^1[1-9]\\d{9}$|^(0\\d{2,3}\\-){0,1}[1-9]\\d{6,7}$", message = "tel内容错误")
    private String tel;

    @Email(message = "email不正确")
    private String email;

    @Size(max = 20, message = "desc不能超过20个字符")
    private String desc;
}

