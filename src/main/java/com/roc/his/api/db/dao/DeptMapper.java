package com.roc.his.api.db.dao;

import com.roc.his.api.db.pojo.DeptEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author grocsoar
 * @description 针对表【tb_dept(部门表)】的数据库操作Mapper
 * @createDate 2025-02-25 22:13:48
 * @Entity com.roc.his.api.db.pojo.DeptEntity
 */
public interface DeptMapper {

    List<HashMap> searchAllDept();

    List<HashMap> searchByPage(Map param);

    long searchCount(Map param);

    int insert(DeptEntity dept);

    HashMap searchById(int id);

    int update(DeptEntity dept);

    boolean searchCanDelete(Integer[] ids);

    int deleteByIds(Integer[] ids);
}




