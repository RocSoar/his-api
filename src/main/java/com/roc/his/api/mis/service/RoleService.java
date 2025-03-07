package com.roc.his.api.mis.service;

import com.roc.his.api.common.PageUtils;
import com.roc.his.api.db.pojo.RoleEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface RoleService {

    List<HashMap> searchAllRole();

    PageUtils searchByPage(Map param);

    int insert(RoleEntity role);

    HashMap searchById(int id);

    List<Integer> searchUserIdByRoleId(int roleId);

    int update(RoleEntity role);

    int deleteByIds(Integer[] ids);
}
