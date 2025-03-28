package com.roc.his.api.mis.service;

import com.roc.his.api.common.PageUtils;
import com.roc.his.api.db.pojo.RuleEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface RuleService {
    List<HashMap> searchAllRule();

    PageUtils searchByPage(Map param);

    int insert(RuleEntity entity);

    HashMap searchById(int id);

    int update(RuleEntity entity);

    int deleteById(int id);

}
