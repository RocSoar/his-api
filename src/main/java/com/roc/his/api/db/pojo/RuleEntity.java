package com.roc.his.api.db.pojo;

import lombok.Data;

/**
 * @TableName tb_rule
 */
@Data
public class RuleEntity {
    private Integer id;

    private String name;

    private String rule;

    private String remark;
}