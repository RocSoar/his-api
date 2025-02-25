package com.roc.his.api.db.pojo;

import lombok.Data;

/**
 * @TableName tb_flow_regulation
 */
@Data
public class FlowRegulationEntity {
    private Integer id;

    private String place;

    private Integer realNum;

    private Integer maxNum;

    private Integer weight;

    private Integer priority;

    private String blueUuid;
}