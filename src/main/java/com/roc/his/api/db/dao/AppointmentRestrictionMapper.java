package com.roc.his.api.db.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author grocsoar
 * @description 针对表【tb_appointment_restriction(体检预约限流表)】的数据库操作Mapper
 * @createDate 2025-02-25 22:13:48
 * @Entity com.roc.his.api.db.pojo.AppointmentRestrictionEntity
 */
public interface AppointmentRestrictionMapper {
    List<HashMap> searchScheduleInRange(Map param);

    int saveOrUpdateRealNum(Map param);
}




