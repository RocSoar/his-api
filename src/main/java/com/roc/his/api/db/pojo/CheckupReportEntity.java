package com.roc.his.api.db.pojo;

import lombok.Data;

/**
 * @TableName tb_checkup_report
 */
@Data
public class CheckupReportEntity {
    private Integer id;

    private Integer appointmentId;

    private String resultId;

    private Integer status;

    private String filePath;

    private String waybillCode;

    private String waybillDate;

    private String date;

    private String createTime;
}