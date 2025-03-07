package com.roc.his.api.mis.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.digest.MD5;
import com.roc.his.api.common.PageUtils;
import com.roc.his.api.db.dao.UserMapper;
import com.roc.his.api.db.pojo.UserEntity;
import com.roc.his.api.mis.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;

    @Override
    public Integer login(Map<String, String> param) {
        String password = param.get("password");

        MD5 md5 = new MD5();

        String temp = md5.digestHex(password);
        String start = temp.substring(0, 6);
        String end = temp.substring(temp.length() - 3);
        String p = md5.digestHex(start + password + end).toUpperCase();
        param.replace("password", p);

        return userMapper.login(param);
    }

    @Override
    public int updatePassword(Map param) {
        String password = MapUtil.getStr(param, "password");
        String newPassword = MapUtil.getStr(param, "newPassword");

        MD5 md5 = new MD5();
        String temp1 = md5.digestHex(password);
        String temp2 = md5.digestHex(newPassword);

        String start1 = temp1.substring(0, 6);
        String end1 = temp1.substring(temp1.length() - 3);
        String start2 = temp2.substring(0, 6);
        String end2 = temp2.substring(temp2.length() - 3);

        String p1 = md5.digestHex(start1 + password + end1).toUpperCase();
        String p2 = md5.digestHex(start2 + newPassword + end2).toUpperCase();

        param.replace("password", p1);
        param.replace("newPassword", p2);
        return userMapper.updatePassword(param);
    }

    @Override
    public PageUtils searchByPage(Map param) {
        List list = new ArrayList<>();

        long count = userMapper.searchCount(param);

        if (count > 0) {
            list = userMapper.searchByPage(param);
        }
        int page = MapUtil.getInt(param, "page");
        int pageSize = MapUtil.getInt(param, "length");

        return new PageUtils(list, count, page, pageSize);
    }

    @Override
    @Transactional
    public int insert(UserEntity user) {
        MD5 md5 = new MD5();
        String password = user.getPassword();

        String temp = md5.digestHex(password);
        String start = temp.substring(0, 6);
        String end = temp.substring(temp.length() - 3);
        String p = md5.digestHex(start + password + end).toUpperCase();

        user.setPassword(p);
        return userMapper.insert(user);
    }

    @Override
    public HashMap searchById(int userId) {
        return userMapper.searchById(userId);
    }

    @Override
    @Transactional
    public int update(Map param) {
        return userMapper.update(param);
    }

    @Override
    @Transactional
    public int deleteByIds(Integer[] ids) {
        return userMapper.deleteByIds(ids);
    }

    @Override
    @Transactional
    public int dismiss(int userId) {
        return userMapper.dismiss(userId);
    }
}
