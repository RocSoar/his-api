package com.roc.his.api.db.pojo;

import lombok.Data;

/**
 * @TableName tb_customer_im
 */
@Data
public class CustomerImEntity {
    private Integer id;

    private Integer customerId;

    private String loginTime;
}