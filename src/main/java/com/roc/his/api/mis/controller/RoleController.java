package com.roc.his.api.mis.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.roc.his.api.common.PageUtils;
import com.roc.his.api.common.R;
import com.roc.his.api.db.pojo.RoleEntity;
import com.roc.his.api.mis.dto.*;
import com.roc.his.api.mis.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mis/role")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @GetMapping("/searchAllRole")
    public R searchAllRole() {
        return R.ok().put("list", roleService.searchAllRole());
    }

    @PostMapping("/searchByPage")
    @SaCheckPermission(value = {"ROOT", "ROLE:SELECT"}, mode = SaMode.OR)
    public R searchByPage(@Valid @RequestBody SearchRoleByPageDTO dto) {
        int page = dto.getPage();
        int pageSize = dto.getLength();
        int start = (page - 1) * pageSize;
        Map param = BeanUtil.beanToMap(dto);
        param.put("start", start);
        PageUtils pageUtils = roleService.searchByPage(param);
        return R.ok().put("page", pageUtils);
    }

    @PostMapping("/insert")
    @SaCheckPermission(value = {"ROOT", "ROLE:INSERT"}, mode = SaMode.OR)
    public R insert(@Valid @RequestBody InsertRoleDTO dto) {
        RoleEntity role = new RoleEntity();
        role.setRoleName(dto.getRoleName());
        role.setPermissions(JSONUtil.parseArray(dto.getPermissions()).toString());
        role.setDesc(dto.getDesc());
        int rows = roleService.insert(role);
        return R.ok().put("rows", rows);
    }

    @PostMapping("/searchById")
    @SaCheckPermission(value = {"ROOT", "ROLE:SELECT"}, mode = SaMode.OR)
    public R searchById(@Valid @RequestBody SearchRoleByIdDTO dto) {
        HashMap map = roleService.searchById(dto.getId());
        return R.ok().put("result", map);
    }

    @PostMapping("/update")
    @SaCheckPermission(value = {"ROOT", "ROLE:UPDATE"}, mode = SaMode.OR)
    public R update(@Valid @RequestBody UpdateRoleDTO dto) {
        RoleEntity role = new RoleEntity();
        role.setId(dto.getId());
        role.setRoleName(dto.getRoleName());
        role.setPermissions(JSONUtil.parseArray(dto.getPermissions()).toString());
        role.setDesc(dto.getDesc());
        int rows = roleService.update(role);
        //如果角色修改成功，并且用户修改了该角色的关联权限
        if (rows == 1 && dto.getChanged()) {
            //把该角色关联的用户踢下线
            List<Integer> list = roleService.searchUserIdByRoleId(dto.getId());
            list.forEach(StpUtil::logout);
        }
        return R.ok().put("rows", rows);
    }

    @PostMapping("/deleteByIds")
    @SaCheckPermission(value = {"ROOT", "ROLE:DELETE"}, mode = SaMode.OR)
    public R deleteByIds(@Valid @RequestBody DeleteRoleByIdsDTO dto) {
        int rows = roleService.deleteByIds(dto.getIds());
        return R.ok().put("rows", rows);
    }
}
