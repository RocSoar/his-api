package com.roc.his.api.db.pojo;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

/**
 * @TableName tb_goods
 */
@Data
@Builder
public class GoodsEntity {
    private Integer id;

    private String code;

    private String title;

    private String description;

    private String checkup_1;

    private String checkup_2;

    private String checkup_3;

    private String checkup_4;

    private String checkup;

    private String image;

    private BigDecimal initialPrice;

    private BigDecimal currentPrice;

    private Integer salesVolume;

    private String type;

    private String tag;

    private Integer partId;

    private Integer ruleId;

    private Integer status;

    private String md5;

    private String updateTime;

    private String createTime;
}