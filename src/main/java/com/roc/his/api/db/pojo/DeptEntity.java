package com.roc.his.api.db.pojo;

import lombok.Data;

/**
 * @TableName tb_dept
 */
@Data
public class DeptEntity {
    private Integer id;

    private String deptName;

    private String tel;

    private String email;

    private String desc;
}