package com.roc.his.api.mis.service.impl;

import cn.hutool.core.map.MapUtil;
import com.roc.his.api.common.PageUtils;
import com.roc.his.api.db.dao.DeptMapper;
import com.roc.his.api.db.pojo.DeptEntity;
import com.roc.his.api.exception.HisException;
import com.roc.his.api.mis.service.DeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeptServiceImpl implements DeptService {
    private final DeptMapper deptMapper;

    @Override
    public List<HashMap> searchAllDept() {
        return deptMapper.searchAllDept();
    }

    @Override
    public PageUtils searchByPage(HashMap param) {
        List<HashMap> list = new ArrayList<>();
        long count = deptMapper.searchCount(param);
        if (count > 0) {
            list = deptMapper.searchByPage(param);
        }
        int page = MapUtil.getInt(param, "page");
        int pageSize = MapUtil.getInt(param, "length");
        PageUtils pageUtils = new PageUtils(list, count, page, pageSize);

        return pageUtils;
    }

    @Override
    @Transactional
    public int insert(DeptEntity dept) {
        return deptMapper.insert(dept);
    }

    @Override
    public HashMap searchById(int id) {
        return deptMapper.searchById(id);
    }

    @Override
    @Transactional
    public int update(DeptEntity dept) {
        return deptMapper.update(dept);
    }

    @Override
    @Transactional
    public int deleteByIds(Integer[] ids) {
        if (!deptMapper.searchCanDelete(ids)) {
            throw new HisException("无法删除关联用户的部门");
        }
        int rows = deptMapper.deleteByIds(ids);
        return rows;
    }
}
