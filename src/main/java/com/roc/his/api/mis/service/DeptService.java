package com.roc.his.api.mis.service;

import com.roc.his.api.common.PageUtils;
import com.roc.his.api.db.pojo.DeptEntity;

import java.util.HashMap;
import java.util.List;

public interface DeptService {
    List<HashMap> searchAllDept();

    PageUtils searchByPage(HashMap param);

    int insert(DeptEntity dept);

    HashMap searchById(int id);

    int update(DeptEntity dept);

    int deleteByIds(Integer[] ids);
}
