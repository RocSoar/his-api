package com.roc.his.api.schedule;

import cn.hutool.core.map.MapUtil;
import com.roc.his.api.async.PaymentWorkAsync;
import com.roc.his.api.db.dao.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderSchedule {
    private final OrderMapper orderMapper;
    private final PaymentWorkAsync paymentWorkAsync;

    /**
     * 每小时执行一次，关闭超过半小时未付款的订单
     */
    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void closeUnpaymentOrder() {
        int rows = orderMapper.closeOrder();
        if (rows > 0) {
            log.info("关闭了" + rows + "个未付款的订单");
        }
    }

    /**
     * 每小时执行一次，处理未收到退款通知消息的订单
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void closeTimeoutRefundOrder() {
        //查询所有未收到退款通知消息的订单
        List<HashMap> list = orderMapper.searchTimeoutRefund();
        list.forEach(map -> {
            int id = MapUtil.getInt(map, "id");
            String outRefundNo = MapUtil.getStr(map, "outRefundNo");
            //让异步线程查询退款结果，避免阻塞for循环
            paymentWorkAsync.closeTimeoutRefund(id, outRefundNo);
        });
    }
}

