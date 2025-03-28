package com.roc.his.api.front.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.roc.his.api.common.R;
import com.roc.his.api.config.sa_token.StpCustomerUtil;
import com.roc.his.api.front.service.CustomerImService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController("FrontCustomerImController")
@RequestMapping("/front/customer/im")
@RequiredArgsConstructor
public class CustomerImController {
    private final CustomerImService customerImService;

    @GetMapping("/createAccount")
    @SaCheckLogin(type = StpCustomerUtil.TYPE)
    public R createAccount() {
        int customerId = StpCustomerUtil.getLoginIdAsInt();
        HashMap result = customerImService.createAccount(customerId);
        return R.ok().put("result", result);
    }
}

