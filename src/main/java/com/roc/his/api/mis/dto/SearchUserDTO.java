package com.roc.his.api.mis.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class SearchUserDTO {
    @NotNull(message = "page不能为空")
    @Min(value = 1, message = "page不能小于1")
    private Integer page;

    @NotNull(message = "pageSize不能为空")
    @Range(min = 10, max = 50, message = "pageSize必须在10~50之间")
    private Integer length;

    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{1,10}$", message = "name必须为长度1-10的中文")
    private String name;

    @Pattern(regexp = "^男$|^女$", message = "sex内容不正确")
    private String sex;

    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]{2,10}$", message = "role内容不正确")
    private String role;

    @Min(value = 1, message = "dept不能小于1")
    private Integer deptId;

    @Min(value = 1, message = "status不能小于1")
    private Integer status;
}
