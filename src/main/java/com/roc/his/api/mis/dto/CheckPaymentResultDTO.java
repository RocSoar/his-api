package com.roc.his.api.mis.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CheckPaymentResultDTO {
    @NotEmpty(message = "outTradeNoArray不能为空")
    private String[] outTradeNoArray;
}

