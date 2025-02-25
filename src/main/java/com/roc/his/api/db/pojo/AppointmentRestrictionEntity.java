package com.roc.his.api.db.pojo;

import lombok.Data;

/**
 * @TableName tb_appointment_restriction
 */
@Data
public class AppointmentRestrictionEntity {
    private Integer id;

    private String date;

    private Integer num1;

    private Integer num2;

    private Integer num3;

    private String remark;

    private String createTime;
}