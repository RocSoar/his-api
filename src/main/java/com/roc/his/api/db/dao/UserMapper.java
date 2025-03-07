package com.roc.his.api.db.dao;

import com.roc.his.api.db.pojo.UserEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author grocsoar
 * @description 针对表【tb_user(用户表)】的数据库操作Mapper
 * @createDate 2025-02-25 22:13:48
 * @Entity com.roc.his.api.db.pojo.UserEntity
 */
@Mapper
public interface UserMapper {

    Set<String> searchUserPermissions(int userId);

    Integer login(Map<String, String> param);

    String searchUsernameById(int userId);

    int updatePassword(Map param);

    List<HashMap> searchByPage(Map param);

    long searchCount(Map param);

    int insert(UserEntity user);

    HashMap searchById(int userId);

    int update(Map param);

    int deleteByIds(Integer[] ids);

    int dismiss(int userId);
}




