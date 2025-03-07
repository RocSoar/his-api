package com.roc.his.api.mis.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.json.JSONUtil;
import com.roc.his.api.common.PageUtils;
import com.roc.his.api.common.R;
import com.roc.his.api.db.pojo.DeptEntity;
import com.roc.his.api.mis.dto.*;
import com.roc.his.api.mis.service.DeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;

@RestController
@RequestMapping("/mis/dept")
@RequiredArgsConstructor
public class DeptController {
    private final DeptService deptService;

    @GetMapping("/searchAllDept")
    public R searchAllDept() {
        return R.ok().put("list", deptService.searchAllDept());
    }

    @PostMapping("/searchByPage")
    @SaCheckPermission(value = {"ROOT", "DEPT:SELECT"}, mode = SaMode.OR)
    public R searchByPage(@Valid @RequestBody SearchDeptByPageDTO dto) {
        int page = dto.getPage();
        int pageSize = dto.getLength();
        int start = (page - 1) * pageSize;
        HashMap param = JSONUtil.parse(dto).toBean(HashMap.class);
        param.put("start", start);
        PageUtils pageUtils = deptService.searchByPage(param);
        return R.ok().put("page", pageUtils);
    }

    @PostMapping("/insert")
    @SaCheckPermission(value = {"ROOT", "DEPT:INSERT"}, mode = SaMode.OR)
    public R insert(@Valid @RequestBody InsertDeptDTO dto) {
        DeptEntity dept = JSONUtil.parse(dto).toBean(DeptEntity.class);
        int rows = deptService.insert(dept);
        return R.ok().put("rows", rows);
    }

    @PostMapping("/searchById")
    @SaCheckPermission(value = {"ROOT", "DEPT:SELECT"}, mode = SaMode.OR)
    public R searchById(@Valid @RequestBody SearchDeptByIdDTO dto) {
        HashMap map = deptService.searchById(dto.getId());
        return R.ok().put("result", map);
    }

    @PostMapping("/update")
    @SaCheckPermission(value = {"ROOT", "DEPT:UPDATE"}, mode = SaMode.OR)
    public R update(@Valid @RequestBody UpdateDeptDTO dto) {
        DeptEntity dept = new DeptEntity();
        dept.setId(dto.getId());
        dept.setDeptName(dto.getDeptName());
        dept.setTel(dto.getTel());
        dept.setEmail(dto.getEmail());
        dept.setDesc(dto.getDesc());
        int rows = deptService.update(dept);
        return R.ok().put("rows", rows);
    }

    @PostMapping("/deleteByIds")
    @SaCheckPermission(value = {"ROOT", "DEPT:DELETE"}, mode = SaMode.OR)
    public R deleteByIds(@Valid @RequestBody DeleteDeptByIdsDTO dto) {
        int rows = deptService.deleteByIds(dto.getIds());
        return R.ok().put("rows", rows);
    }
}
