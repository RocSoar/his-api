package com.roc.his.api.db.dao;

import java.util.HashMap;
import java.util.List;

/**
 * @author grocsoar
 * @description 针对表【tb_permission(权限表)】的数据库操作Mapper
 * @createDate 2025-02-25 22:13:48
 * @Entity com.roc.his.api.db.pojo.PermissionEntity
 */
public interface PermissionMapper {
    List<HashMap> searchAllPermission();
}




