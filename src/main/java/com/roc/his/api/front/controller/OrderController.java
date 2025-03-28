package com.roc.his.api.front.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.felord.payment.wechat.v3.WechatApiProvider;
import cn.felord.payment.wechat.v3.model.ResponseSignVerifyParams;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import com.roc.his.api.common.PageUtils;
import com.roc.his.api.common.R;
import com.roc.his.api.config.sa_token.StpCustomerUtil;
import com.roc.his.api.front.dto.*;
import com.roc.his.api.front.service.OrderService;
import com.roc.his.api.socket.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController("FrontOrderController")
@RequestMapping("/front/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;
    private final WechatApiProvider wechatApiProvider;

    @PostMapping("/createPayment")
    @SaCheckLogin(type = StpCustomerUtil.TYPE)
    public R createPayment(@RequestBody @Valid CreatePaymentDTO dto) {
        int customerId = StpCustomerUtil.getLoginIdAsInt();
        Map param = BeanUtil.beanToMap(dto);
        param.put("customerId", customerId);
        HashMap map = orderService.createPayment(param);
        if (map == null) {
            return R.ok().put("illegal", true);
        } else {
            return R.ok().put("illegal", false).put("result", map);
        }
    }

    @SneakyThrows
    @PostMapping("/paymentCallback")
    public Map paymentCallback(
            @RequestHeader("Wechatpay-Serial") String serial,
            @RequestHeader("Wechatpay-Signature") String signature,
            @RequestHeader("Wechatpay-Timestamp") String timestamp,
            @RequestHeader("Wechatpay-Nonce") String nonce,
            HttpServletRequest request) {
        String body = request.getReader().lines().collect(Collectors.joining());
        // 对请求头进行验签 以确保是微信服务器的调用
        ResponseSignVerifyParams params = new ResponseSignVerifyParams();
        params.setWechatpaySerial(serial);
        params.setWechatpaySignature(signature);
        params.setWechatpayTimestamp(timestamp);
        params.setWechatpayNonce(nonce);
        params.setBody(body);
        return wechatApiProvider.callback("his-vue").transactionCallback(params,
                data -> {
                    String transactionId = data.getTransactionId();
                    String outTradeNo = data.getOutTradeNo();
                    //更新订单状态和微信交易单ID
                    boolean bool = orderService.updatePayment(
                            Map.of("outTradeNo", outTradeNo, "transactionId", transactionId));
                    // 用WebSocket通知前端项目付款成功
                    if (!bool) {
                        log.error("订单付款成功，但是状态更新失败");
                        return;
                    }
                    log.debug("订单付款成功，已更新订单状态");
                    //查询订单的customerId
                    Integer customerId = orderService.searchCustomerId(outTradeNo);
                    if (customerId == null) {
                        log.error("没有查询到customerId");
                        return;
                    }
                    //推送付款成功消息给前端页面
                    JSONObject json = new JSONObject();
                    json.set("result", true);
                    WebSocketService.sendInfo(json.toString(), "customer_" + customerId.toString());
                });
    }

    @PostMapping("/searchPaymentResult")
    @SaCheckLogin(type = StpCustomerUtil.TYPE)
    public R searchPaymentResult(@Valid @RequestBody SearchPaymentResultDTO dto) {
        boolean bool = orderService.searchPaymentResult(dto.getOutTradeNo());
        return R.ok().put("result", bool);
    }

    @PostMapping("/searchByPage")
    @SaCheckLogin(type = StpCustomerUtil.TYPE)
    public R searchByPage(@RequestBody @Valid SearchOrderByPageDTO dto) {
        int customerId = StpCustomerUtil.getLoginIdAsInt();
        int currentPage = dto.getPage();
        int pageSize = dto.getLength();
        int start = (currentPage - 1) * pageSize;
        Map param = BeanUtil.beanToMap(dto);
        param.put("start", start);
        param.put("customerId", customerId);
        PageUtils pageUtils = orderService.searchByPage(param);
        return R.ok().put("page", pageUtils);
    }

    @PostMapping("/refund")
    @SaCheckLogin(type = StpCustomerUtil.TYPE)
    public R refund(@RequestBody @Valid RefundDTO dto) {
        int customerId = StpCustomerUtil.getLoginIdAsInt();

        Map param = BeanUtil.beanToMap(dto);
        param.put("customerId", customerId);

        boolean bool = orderService.refund(param);
        return R.ok().put("result", bool);
    }

    @SneakyThrows
    @PostMapping("/refundCallback")
    public Map refundCallback(
            @RequestHeader("Wechatpay-Serial") String serial,
            @RequestHeader("Wechatpay-Signature") String signature,
            @RequestHeader("Wechatpay-Timestamp") String timestamp,
            @RequestHeader("Wechatpay-Nonce") String nonce,
            HttpServletRequest request) {
        String body = request.getReader().lines().collect(Collectors.joining());
        //验证数字签名，确保是微信服务器发送的通知消息
        ResponseSignVerifyParams params = new ResponseSignVerifyParams();
        params.setWechatpaySerial(serial);
        params.setWechatpaySignature(signature);
        params.setWechatpayTimestamp(timestamp);
        params.setWechatpayNonce(nonce);
        params.setBody(body);

        return wechatApiProvider.callback("his-vue").refundCallback(params, data -> {
            //判断退款是否成功
            String status = data.getRefundStatus().toString();
            if ("SUCCESS".equals(status)) {
                String outRefundNo = data.getOutRefundNo();
                //把订单更新成已退款状态
                boolean bool = orderService.updateRefundStatus(outRefundNo);
                if (!bool) {
                    log.error("订单状态更新失败");
                } else {
                    log.info("退款流水号为" + outRefundNo + "的订单退款成功");
                }
            } else if ("ABNORMAL".equals(status)) {
                //TODO:用户银行卡作废或者冻结，发送短信给用户手机，让用户联系客服执行手动退款到其他银行卡
            }
        });
    }

    @PostMapping("/payOrder")
    @SaCheckLogin(type = StpCustomerUtil.TYPE)
    public R payOrder(@RequestBody @Valid PayOrderDTO dto) {
        int customerId = StpCustomerUtil.getLoginIdAsInt();
        String qrCodeBase64 = orderService.payOrder(customerId, dto.getOutTradeNo());
        return R.ok().put("result", qrCodeBase64 != null).put("qrCodeBase64", qrCodeBase64);
    }

    @PostMapping("/closeOrderById")
    @SaCheckLogin(type = StpCustomerUtil.TYPE)
    public R closeOrderById(@RequestBody @Valid CloseOrderByIdDTO dto) {
        int customerId = StpCustomerUtil.getLoginIdAsInt();
        Map param = BeanUtil.beanToMap(dto);
        param.put("customerId", customerId);
        boolean bool = orderService.closeOrderById(param);
        return R.ok().put("result", bool);
    }
}









