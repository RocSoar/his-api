package com.roc.his.api.mis.service.impl;

import cn.hutool.core.map.MapUtil;
import com.roc.his.api.common.PageUtils;
import com.roc.his.api.db.dao.OrderMapper;
import com.roc.his.api.front.service.PaymentService;
import com.roc.his.api.mis.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service("MisOrderService")
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final PaymentService paymentService;

    @Override
    public PageUtils searchByPage(Map param) {
        List<HashMap> list = new ArrayList<>();
        long totalCount = orderMapper.searchCount(param);
        if (totalCount > 0) {
            list = orderMapper.searchByPage(param);
        }
        int currentPage = MapUtil.getInt(param, "page");
        int pageSize = MapUtil.getInt(param, "length");
        PageUtils pageUtils = new PageUtils(list, totalCount, currentPage, pageSize);

        return pageUtils;
    }

    @Override
    @Transactional
    public int checkPaymentResult(String[] outTradeNoArray) {
        return Arrays.stream(outTradeNoArray)
                .mapToInt(outTradeNo -> {
                    String transactionId = paymentService.searchPaymentResult(outTradeNo);
                    if (transactionId == null) return 0;
                    return orderMapper.updatePayment(Map.of("outTradeNo", outTradeNo, "transactionId", transactionId));
                })
                .sum();
    }

    @Override
    @Transactional
    public int deleteById(int id) {
        int rows = orderMapper.deleteById(id);
        return rows;
    }

    @Override
    @Transactional
    public int updateRefundStatusById(int id) {
        int rows = orderMapper.updateRefundStatusById(id);
        return rows;
    }
}

