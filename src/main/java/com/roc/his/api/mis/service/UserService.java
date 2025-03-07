package com.roc.his.api.mis.service;

import com.roc.his.api.common.PageUtils;
import com.roc.his.api.db.pojo.UserEntity;

import java.util.HashMap;
import java.util.Map;

public interface UserService {
    Integer login(Map<String, String> param);

    int updatePassword(Map param);

    PageUtils searchByPage(Map param);

    int insert(UserEntity user);

    HashMap searchById(int userId);

    int update(Map param);

    int deleteByIds(Integer[] ids);

    int dismiss(int userId);

}
