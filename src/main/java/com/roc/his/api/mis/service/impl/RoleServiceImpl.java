package com.roc.his.api.mis.service.impl;

import cn.hutool.core.map.MapUtil;
import com.roc.his.api.common.PageUtils;
import com.roc.his.api.db.dao.RoleMapper;
import com.roc.his.api.db.pojo.RoleEntity;
import com.roc.his.api.exception.HisException;
import com.roc.his.api.mis.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleMapper roleMapper;

    @Override
    public List<HashMap> searchAllRole() {
        return roleMapper.searchAllRole();
    }

    @Override
    public PageUtils searchByPage(Map param) {
        List<HashMap> list = new ArrayList<>();
        long count = roleMapper.searchCount(param);
        if (count > 0) {
            list = roleMapper.searchByPage(param);
        }
        int page = MapUtil.getInt(param, "page");
        int pageSize = MapUtil.getInt(param, "length");
        PageUtils pageUtils = new PageUtils(list, count, page, pageSize);
        return pageUtils;
    }

    @Override
    @Transactional
    public int insert(RoleEntity role) {
        return roleMapper.insert(role);
    }

    @Override
    public HashMap searchById(int id) {
        return roleMapper.searchById(id);
    }

    @Override
    public List<Integer> searchUserIdByRoleId(int roleId) {
        return roleMapper.searchUserIdByRoleId(roleId);
    }

    @Override
    @Transactional
    public int update(RoleEntity role) {
        return roleMapper.update(role);
    }

    @Override
    @Transactional
    public int deleteByIds(Integer[] ids) {
        if (!roleMapper.searchCanDelete(ids)) {
            throw new HisException("无法删除关联用户的角色");
        }
        int rows = roleMapper.deleteByIds(ids);
        return rows;
    }
}
