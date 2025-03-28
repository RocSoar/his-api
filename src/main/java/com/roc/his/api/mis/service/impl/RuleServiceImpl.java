package com.roc.his.api.mis.service.impl;

import cn.hutool.core.map.MapUtil;
import com.roc.his.api.common.PageUtils;
import com.roc.his.api.db.dao.RuleMapper;
import com.roc.his.api.db.pojo.RuleEntity;
import com.roc.his.api.mis.service.RuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RuleServiceImpl implements RuleService {
    private final RuleMapper ruleMapper;

    @Override
    public List<HashMap> searchAllRule() {
        List<HashMap> list = ruleMapper.searchAllRule();
        return list;
    }

    @Override
    public PageUtils searchByPage(Map param) {
        List<HashMap> list = new ArrayList<>();
        long count = ruleMapper.searchCount(param);
        if (count > 0) {
            list = ruleMapper.searchByPage(param);
        }
        int page = MapUtil.getInt(param, "page");
        int length = MapUtil.getInt(param, "length");
        PageUtils pageUtils = new PageUtils(list, count, page, length);
        return pageUtils;
    }

    @Override
    @Transactional
    public int insert(RuleEntity entity) {
        int rows = ruleMapper.insert(entity);
        return rows;
    }

    @Override
    public HashMap searchById(int id) {
        return ruleMapper.searchById(id);
    }

    @Override
    @Transactional
    public int update(RuleEntity entity) {
        return ruleMapper.update(entity);
    }

    @Override
    @Transactional
    public int deleteById(int id) {
        int rows = ruleMapper.deleteById(id);
        return rows;
    }
}
