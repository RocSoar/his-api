package com.roc.his.api.mis.service;

import com.roc.his.api.common.PageUtils;

import java.util.Map;

public interface OrderService {
    PageUtils searchByPage(Map param);

    int checkPaymentResult(String[] outTradeNoArray);

    int deleteById(int id);

    int updateRefundStatusById(int id);

}

