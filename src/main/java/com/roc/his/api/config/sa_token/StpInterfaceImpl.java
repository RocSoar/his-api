package com.roc.his.api.config.sa_token;

import cn.dev33.satoken.stp.StpInterface;
import com.roc.his.api.db.dao.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {
    private final UserMapper userMapper;

    /**
     * 返回一个用户所拥有的权限集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        int userId = Integer.parseInt(loginId.toString());
        Set<String> permissions = userMapper.searchUserPermissions(userId);

        return new ArrayList<>(permissions);
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return List.of();
    }
}
