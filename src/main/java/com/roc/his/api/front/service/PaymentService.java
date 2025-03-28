package com.roc.his.api.front.service;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface PaymentService {
    ObjectNode unifiedOrder(String outTradeNo, int total,
                            String desc, String notifyUrl,
                            String timeExpire);

    String searchPaymentResult(String outTradeNo);

    String refund(String transactionId, Integer refund, Integer total, String notifyUrl);

    String searchRefundResult(String outRefundNo);


}

