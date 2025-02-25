package com.roc.his.api.db.pojo;

import lombok.Data;

/**
 * @TableName tb_user
 */
@Data
public class UserEntity {
    private Integer id;

    private String username;

    private String password;

    private String openId;

    private String photo;

    private String name;

    private String sex;

    private String tel;

    private String email;

    private String hiredate;

    private String role;

    private Integer root;

    private Integer deptId;

    private Integer status;

    private String createTime;
}