package com.roc.his.api.mis.service.impl;

import com.roc.his.api.db.dao.CustomerMapper;
import com.roc.his.api.db.dao.OrderMapper;
import com.roc.his.api.mis.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service("MisCustomerService")
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerMapper customerMapper;
    private final OrderMapper orderMapper;

    @Override
    public HashMap searchSummary(int id) {
        HashMap map = customerMapper.searchById(id);
        map.putAll(orderMapper.searchFrontStatistic(id));
        return map;
    }
}

