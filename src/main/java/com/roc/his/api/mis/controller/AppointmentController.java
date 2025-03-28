package com.roc.his.api.mis.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.bean.BeanUtil;
import com.roc.his.api.common.PageUtils;
import com.roc.his.api.common.R;
import com.roc.his.api.mis.dto.DeleteAppointmentByIdsDTO;
import com.roc.his.api.mis.dto.HasAppointInTodayDTO;
import com.roc.his.api.mis.dto.SearchAppointmentByOrderIdDTO;
import com.roc.his.api.mis.dto.SearchAppointmentByPageDTO;
import com.roc.his.api.mis.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController("MisAppointmentController")
@RequestMapping("/mis/appointment")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;

    @PostMapping("/searchByOrderId")
    @SaCheckPermission(value = {"ROOT", "APPOINTMENT:SELECT"}, mode = SaMode.OR)
    public R searchByOrderId(@RequestBody @Valid SearchAppointmentByOrderIdDTO dto) {
        List<HashMap> list = appointmentService.searchByOrderId(dto.getOrderId());
        return R.ok().put("result", list);
    }

    @PostMapping("/searchByPage")
    @SaCheckPermission(value = {"ROOT", "APPOINTMENT:SELECT"}, mode = SaMode.OR)
    public R searchByPage(@RequestBody @Valid SearchAppointmentByPageDTO dto) {
        int page = dto.getPage();
        int length = dto.getLength();
        int start = (page - 1) * length;
        Map param = BeanUtil.beanToMap(dto);
        param.put("start", start);
        PageUtils pageUtils = appointmentService.searchByPage(param);
        return R.ok().put("page", pageUtils);
    }

    @PostMapping("/deleteByIds")
    @SaCheckPermission(value = {"ROOT", "APPOINTMENT:DELETE"}, mode = SaMode.OR)
    public R deleteByIds(@RequestBody @Valid DeleteAppointmentByIdsDTO dto) {
        int rows = appointmentService.deleteByIds(dto.getIds());
        return R.ok().put("rows", rows);
    }

    @PostMapping("/hasAppointInToday")
    @SaCheckPermission(value = {"ROOT", "APPOINTMENT:UPDATE"}, mode = SaMode.OR)
    public R hasAppointInToday(@RequestBody @Valid HasAppointInTodayDTO form) {
        Map param = BeanUtil.beanToMap(form);
        int result = appointmentService.hasAppointInToday(param);
        return R.ok().put("result", result);
    }
}

