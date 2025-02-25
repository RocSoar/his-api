package com.roc.his.api.db.pojo;

import lombok.Data;

/**
 * @TableName tb_appointment
 */
@Data
public class AppointmentEntity {
    private Integer id;

    private String uuid;

    private Integer orderId;

    private String date;

    private String name;

    private String sex;

    private String pid;

    private String birthday;

    private String tel;

    private String mailingAddress;

    private String company;

    private Integer status;

    private String checkinTime;

    private String createTime;
}