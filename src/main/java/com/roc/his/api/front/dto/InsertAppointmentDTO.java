package com.roc.his.api.front.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class InsertAppointmentDTO {
    @NotNull(message = "orderId不能为空")
    @Min(value = 1, message = "orderId不能小于1")
    private Integer orderId;

    @NotBlank(message = "date不能为空")
    @Pattern(regexp = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29))$", message = "date内容不正确")
    private String date;

    @NotBlank(message = "name不能为空")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{2,10}$", message = "name内容不正确")
    private String name;

    @NotBlank(message = "pid不能为空")
    @Pattern(regexp = "^[0-9Xx]{18}$", message = "pid内容不正确")
    private String pid;

    @NotBlank(message = "tel不能为空")
    @Pattern(regexp = "^1[1-9]\\d{9}$", message = "tel内容不正确")
    private String tel;

    @NotBlank(message = "mailingAddress不能为空")
    @Pattern(regexp = "^[0-9A-Za-z\\u4e00-\\u9fa5\\-_#]{10,100}", message = "mailingAddress内容不正确")
    private String mailingAddress;

    @Pattern(regexp = "^[0-9A-Za-z\\u4e00-\\u9fa5\\-_#]{2,100}", message = "company内容不正确")
    private String company;
}

