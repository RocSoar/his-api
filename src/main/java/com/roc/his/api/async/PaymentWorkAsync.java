package com.roc.his.api.async;

import com.roc.his.api.db.dao.OrderMapper;
import com.roc.his.api.exception.HisException;
import com.roc.his.api.front.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentWorkAsync {
    private final OrderMapper orderMapper;
    private final PaymentService paymentService;

    @Async("AsyncTaskExecutor") //找到线程池，该方法的执行会被线程池分配给空闲的线程
    @Transactional
    public void closeTimeoutRefund(int id, String outRefundNo) {
        //主动查询退款结果
        String result = paymentService.searchRefundResult(outRefundNo);
        if ("SUCCESS".equals(result)) {
            //更新订单状态为已退款
            int rows = orderMapper.updateRefundStatusById(id);
            if (rows != 1) {
                throw new HisException("订单更新为已退款状态失败");
            }
            log.info("订单 {} 主动查询退款成功, 已更新退款状态", id);
        } else if ("ABNORMAL".equals(result)) {
            /* TODO:
             * 1.先判断是否给用户发送过退款失败短信
             * 2.如果没有发送过短信，就给用户发送退款失败短信
             */
        }
    }
}

