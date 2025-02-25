package com.roc.his.api.db.pojo;

import lombok.Data;

/**
 * @TableName tb_customer_location
 */
@Data
public class CustomerLocationEntity {
    private Integer id;

    private Integer customerId;

    private String blueUuid;

    private Integer placeId;

    private String createTime;
}