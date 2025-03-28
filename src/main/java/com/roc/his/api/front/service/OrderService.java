package com.roc.his.api.front.service;

import com.roc.his.api.common.PageUtils;

import java.util.HashMap;
import java.util.Map;

public interface OrderService {
    HashMap createPayment(Map param);

    boolean updatePayment(Map param);

    Integer searchCustomerId(String outTradeNo);

    boolean searchPaymentResult(String outTradeNo);

    PageUtils searchByPage(Map param);

    boolean refund(Map param);

    boolean updateRefundStatus(String outRefundNo);

    String payOrder(int customerId, String outTradeNo);

    boolean closeOrderById(Map param);

    boolean hasOwnOrder(Map param);


}

