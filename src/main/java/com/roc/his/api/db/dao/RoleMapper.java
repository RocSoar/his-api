package com.roc.his.api.db.dao;


import com.roc.his.api.db.pojo.RoleEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author grocsoar
 * @description 针对表【tb_role(角色表)】的数据库操作Mapper
 * @createDate 2025-02-25 22:13:48
 * @Entity com.roc.his.api.db.pojo.RoleEntity
 */
public interface RoleMapper {

    List<HashMap> searchAllRole();

    List<HashMap> searchByPage(Map param);

    long searchCount(Map param);

    int insert(RoleEntity role);

    HashMap searchById(int id);

    List<Integer> searchUserIdByRoleId(int roleId);

    int update(RoleEntity role);

    boolean searchCanDelete(Integer[] ids);

    int deleteByIds(Integer[] ids);
}




