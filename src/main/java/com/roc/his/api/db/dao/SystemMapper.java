package com.roc.his.api.db.dao;

import com.roc.his.api.db.pojo.SystemEntity;

import java.util.List;

/**
 * @author grocsoar
 * @description 针对表【tb_system(系统表)】的数据库操作Mapper
 * @createDate 2025-02-25 22:13:48
 * @Entity com.roc.his.api.db.pojo.SystemEntity
 */
public interface SystemMapper {
    List<SystemEntity> searchAll();
}




