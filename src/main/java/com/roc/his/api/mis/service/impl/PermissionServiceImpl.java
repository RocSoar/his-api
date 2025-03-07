package com.roc.his.api.mis.service.impl;

import com.roc.his.api.db.dao.PermissionMapper;
import com.roc.his.api.mis.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final PermissionMapper permissionMapper;

    @Override
    public List<HashMap> searchAllPermission() {
        return permissionMapper.searchAllPermission();
    }
}
