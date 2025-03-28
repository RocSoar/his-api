package com.roc.his.api.db.dao;

import com.roc.his.api.db.pojo.RuleEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author grocsoar
 * @description 针对表【tb_rule(规则表)】的数据库操作Mapper
 * @createDate 2025-02-25 22:13:48
 * @Entity com.roc.his.api.db.pojo.RuleEntity
 */
public interface RuleMapper {
    List<HashMap> searchAllRule();

    List<HashMap> searchByPage(Map param);

    long searchCount(Map param);

    int insert(RuleEntity entity);

    HashMap searchById(int id);

    int update(RuleEntity entity);

    int deleteById(int id);

}





