package com.roc.his.api.db.pojo;

import lombok.Data;

/**
 * @TableName tb_customer
 */
@Data
public class CustomerEntity {
    private Integer id;

    private String name;

    private String sex;

    private String tel;

    private String photo;

    private String createTime;
}