package com.roc.his.api.db.pojo;

import lombok.Data;

/**
 * @TableName tb_action
 */
@Data
public class ActionEntity {
    private Integer id;

    private String actionCode;

    private String actionName;
}