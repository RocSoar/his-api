package com.roc.his.api.mis.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.bean.BeanUtil;
import com.roc.his.api.common.PageUtils;
import com.roc.his.api.common.R;
import com.roc.his.api.db.pojo.RuleEntity;
import com.roc.his.api.mis.dto.*;
import com.roc.his.api.mis.service.RuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mis/rule")
@RequiredArgsConstructor
public class RuleController {
    private final RuleService ruleService;

    @GetMapping("/searchAllRule")
    @SaCheckLogin
    public R searchAllRule() {
        List<HashMap> list = ruleService.searchAllRule();
        return R.ok().put("result", list);
    }

    @PostMapping("/searchByPage")
    @SaCheckPermission(value = {"ROOT", "RULE:SELECT"}, mode = SaMode.OR)
    public R searchByPage(@RequestBody @Valid SearchRuleByPageDTO dto) {
        int page = dto.getPage();
        int length = dto.getLength();
        int start = (page - 1) * length;
        Map param = BeanUtil.beanToMap(dto);
        param.put("start", start);
        PageUtils pageUtils = ruleService.searchByPage(param);
        return R.ok().put("page", pageUtils);
    }

    @PostMapping("/insert")
    @SaCheckPermission(value = {"ROOT", "RULE:INSERT"}, mode = SaMode.OR)
    public R insert(@RequestBody @Valid InsertRuleDTO dto) {
        RuleEntity entity = BeanUtil.toBean(dto, RuleEntity.class);
        int rows = ruleService.insert(entity);
        return R.ok().put("rows", rows);
    }

    @PostMapping("/searchById")
    @SaCheckPermission(value = {"ROOT", "RULE:SELECT"}, mode = SaMode.OR)
    public R searchById(@RequestBody @Valid SearchRuleByIdDTO dto) {
        HashMap map = ruleService.searchById(dto.getId());
        return R.ok().put("result", map);
    }

    @PostMapping("/update")
    @SaCheckPermission(value = {"ROOT", "RULE:UPDATE"}, mode = SaMode.OR)
    public R update(@RequestBody @Valid UpdateRuleDTO dto) {
        RuleEntity entity = BeanUtil.toBean(dto, RuleEntity.class);
        int rows = ruleService.update(entity);
        return R.ok().put("rows", rows);
    }

    @PostMapping("/deleteById")
    @SaCheckPermission(value = {"ROOT", "RULE:DELETE"}, mode = SaMode.OR)
    public R deleteById(@RequestBody @Valid DeleteRuleByIdDTO dto) {
        int rows = ruleService.deleteById(dto.getId());
        return R.ok().put("rows", rows);
    }
}

