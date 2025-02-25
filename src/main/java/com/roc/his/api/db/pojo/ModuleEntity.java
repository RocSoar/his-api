package com.roc.his.api.db.pojo;

import lombok.Data;

/**
 * @TableName tb_module
 */
@Data
public class ModuleEntity {
    private Integer id;

    private String moduleCode;

    private String moduleName;
}