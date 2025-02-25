package com.roc.his.api.db.pojo;

import java.math.BigDecimal;
import lombok.Data;

/**
 * @TableName tb_order
 */
@Data
public class OrderEntity {
    private Integer id;

    private Integer customerId;

    private Integer goodsId;

    private String snapshotId;

    private String goodsTitle;

    private BigDecimal goodsPrice;

    private Integer number;

    private BigDecimal amount;

    private String goodsImage;

    private String goodsDescription;

    private String outTradeNo;

    private String transactionId;

    private String outRefundNo;

    private Integer status;

    private String createDate;

    private String createTime;

    private String refundDate;

    private String refundTime;
}