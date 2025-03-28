package com.roc.his.api.mis.service;

import com.roc.his.api.common.PageUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface AppointmentService {
    List<HashMap> searchByOrderId(int orderId);

    PageUtils searchByPage(Map param);

    int deleteByIds(Integer[] ids);

    int hasAppointInToday(Map param);

}

