package com.roc.his.api.front.service.impl;

import cn.felord.payment.PayException;
import cn.felord.payment.wechat.v3.WechatApiProvider;
import cn.felord.payment.wechat.v3.WechatResponseEntity;
import cn.felord.payment.wechat.v3.model.*;
import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.roc.his.api.front.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final WechatApiProvider wechatApiProvider;

    @Override
    public ObjectNode unifiedOrder(String outTradeNo, int total, String desc, String notifyUrl, String timeExpire) {
        PayParams payParams = new PayParams();

        Amount amount = new Amount();
//        amount.setTotal(total); //设置付款金额
        amount.setTotal(1); //设置付款金额为1分钱
        payParams.setAmount(amount);

        payParams.setOutTradeNo(outTradeNo); //设置订单流水号
        payParams.setDescription(desc); //设置商品描述信息
        payParams.setNotifyUrl(notifyUrl); //设置通知回调地址

        SceneInfo sceneInfo = new SceneInfo();
        sceneInfo.setPayerClientIp("127.0.0.1"); //终端设备（浏览器）的IP地址, 可随意填写
        payParams.setSceneInfo(sceneInfo);

        if (timeExpire != null) {
            payParams.setTimeExpire(OffsetDateTime.parse(timeExpire));
        }

//        进行支付并获得返回的结果
        WechatResponseEntity<ObjectNode> response = wechatApiProvider.directPayApi("his-vue").nativePay(payParams);
        if (response.is2xxSuccessful()) {
            return response.getBody();
        }
        log.error("创建微信支付订单失败: {}", response.getBody());
        throw new PayException("创建微信支付订单失败");
    }

    @Override
    public String searchPaymentResult(String outTradeNo) {
        TransactionQueryParams params = new TransactionQueryParams();
//        封装订单流水号
        params.setTransactionIdOrOutTradeNo(outTradeNo);

//        主动向微信服务器查询该订单流水号是否已支付
        WechatResponseEntity<ObjectNode> entity = wechatApiProvider.directPayApi("his-vue").queryTransactionByOutTradeNo(params);
        if (!entity.is2xxSuccessful()) {
            log.error("查询付款失败", entity.getBody());
            return null;
        }
        ObjectNode body = entity.getBody();

        String status = body.get("trade_state").textValue();
        if ("SUCCESS".equals(status)) {
//            如果是SUCCESS, 表明已支付, 获取微信支付交易单号
            String transactionId = body.get("transaction_id").textValue();
            return transactionId;
        }
        return null;
    }

    @Override
    public String refund(String transactionId, Integer refund, Integer total, String notifyUrl) {
        RefundParams params = new RefundParams();
        params.setTransactionId(transactionId);
        String outRefundNo = IdUtil.simpleUUID().toUpperCase(); //生成退款流水号
        params.setOutRefundNo(outRefundNo);
        params.setNotifyUrl(notifyUrl);

        RefundParams.RefundAmount amount = new RefundParams.RefundAmount();
        amount.setRefund(refund); //退款金额
        amount.setTotal(total); //订单金额
        amount.setCurrency("CNY");

        params.setAmount(amount);

//        向微信服务器进行退款
        WechatResponseEntity<ObjectNode> entity = wechatApiProvider.directPayApi("his-vue").refund(params);
        if (!entity.is2xxSuccessful()) {
            log.error("退款失败", entity.getBody());
            return null;
        }
        ObjectNode body = entity.getBody();
        //判断微信服务器返回的状态是否是退款处理中
        if ("PROCESSING".equals(body.get("status").textValue())) {
            return outRefundNo;
        }
        return null;
    }

    public String searchRefundResult(String outRefundNo) {
        // 主动向微信服务器查询该订单是否已退款成功
        WechatResponseEntity<ObjectNode> entity = wechatApiProvider.directPayApi("his-vue").queryRefundInfo(outRefundNo);
        if (!entity.is2xxSuccessful()) {
            log.error("查询退款失败", entity.getBody());
            return "FAIL";
        }
        ObjectNode body = entity.getBody();
        String status = body.get("status").textValue();
        if ("SUCCESS".equals(status)) {
            return "SUCCESS";
        } else if ("ABNORMAL".equals(status)) {
            return "ABNORMAL";
        }
        return "FAIL";
    }
}
