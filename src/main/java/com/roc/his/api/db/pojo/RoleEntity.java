package com.roc.his.api.db.pojo;

import lombok.Data;

/**
 * @TableName tb_role
 */
@Data
public class RoleEntity {
    private Integer id;

    private String roleName;

    private String permissions;

    private String desc;

    private String defaultPermissions;

    private Integer systemic;
}