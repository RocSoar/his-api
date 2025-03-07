package com.roc.his.api.mis.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.roc.his.api.common.R;
import com.roc.his.api.mis.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/mis/permission")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    @GetMapping("/searchAllPermission")
    @SaCheckPermission(value = {"ROOT", "ROLE:INSERT", "ROLE:UPDATE"}, mode = SaMode.OR)
    public R searchAllPermission() {
        List<HashMap> list = permissionService.searchAllPermission();
        return R.ok().put("list", list);
    }
}

