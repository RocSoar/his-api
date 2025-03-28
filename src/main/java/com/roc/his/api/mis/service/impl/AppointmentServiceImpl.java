package com.roc.his.api.mis.service.impl;

import cn.hutool.core.map.MapUtil;
import com.roc.his.api.common.PageUtils;
import com.roc.his.api.db.dao.AppointmentMapper;
import com.roc.his.api.mis.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("MisAppointmentService")
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentMapper appointmentMapper;

    @Override
    public List<HashMap> searchByOrderId(int orderId) {
        List<HashMap> list = appointmentMapper.searchByOrderId(orderId);
        return list;
    }

    @Override
    public PageUtils searchByPage(Map param) {
        List<HashMap> list = new ArrayList<>();
        long count = appointmentMapper.searchCount(param);
        if (count > 0) {
            list = appointmentMapper.searchByPage(param);
        }
        int start = (Integer) param.get("start");
        int length = (Integer) param.get("length");
        PageUtils pageUtils = new PageUtils(list, count, start, length);
        return pageUtils;
    }

    @Override
    @Transactional
    public int deleteByIds(Integer[] ids) {
        int rows = appointmentMapper.deleteByIds(ids);
        return rows;
    }

    @Override
    public int hasAppointInToday(Map param) {
        HashMap map = appointmentMapper.hasAppointInToday(param);
        if (map == null) {
            return 0; //今日没有此人的预约
        } else if (MapUtil.getInt(map, "status") != 1) {
            return -1; //已经签到了
        } else {
            return 1; //有预约，未签到
        }
    }
}

