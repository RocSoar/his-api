package com.roc.his.api.front.service;

import com.roc.his.api.common.PageUtils;
import com.roc.his.api.db.pojo.AppointmentEntity;

import java.util.Map;

public interface AppointmentService {

    String insert(AppointmentEntity entity);

    PageUtils searchByPage(Map param);

}
