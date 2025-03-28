package com.roc.his.api.db.dao;

import com.roc.his.api.db.pojo.AppointmentEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author grocsoar
 * @description 针对表【tb_appointment(体检预约表)】的数据库操作Mapper
 * @createDate 2025-02-25 22:13:48
 * @Entity com.roc.his.api.db.pojo.AppointmentEntity
 */
public interface AppointmentMapper {
    List<HashMap> searchByOrderId(int orderId);

    int insert(AppointmentEntity entity);

    List<HashMap> searchFrontAppointmentByPage(Map param);

    long searchFrontAppointmentCount(Map param);

    List<HashMap> searchByPage(Map param);

    long searchCount(Map param);

    int deleteByIds(Integer[] ids);

    HashMap hasAppointInToday(Map param);

}





