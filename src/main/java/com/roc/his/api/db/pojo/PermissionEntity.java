package com.roc.his.api.db.pojo;

import lombok.Data;

/**
 * @TableName tb_permission
 */
@Data
public class PermissionEntity {
    private Integer id;

    private String permissionName;

    private Integer moduleId;

    private Integer actionId;
}