package com.roc.his.api.db.dao;

import com.roc.his.api.db.pojo.CustomerEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author grocsoar
 * @description 针对表【tb_customer(客户表)】的数据库操作Mapper
 * @createDate 2025-02-25 22:13:48
 * @Entity com.roc.his.api.db.pojo.CustomerEntity
 */
public interface CustomerMapper {
    Integer searchIdByTel(String tel);

    void insert(CustomerEntity entity);

    HashMap searchById(int id);

    int update(Map param);
}
