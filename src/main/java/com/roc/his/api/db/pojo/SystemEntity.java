package com.roc.his.api.db.pojo;

import lombok.Data;

/**
 * @TableName tb_system
 */
@Data
public class SystemEntity {
    private Integer id;

    private String item;

    private String value;

    private String remark;
}