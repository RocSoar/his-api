package com.roc.his.api.mis.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.roc.his.api.common.R;
import com.roc.his.api.mis.service.CustomerImService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController("MisCustomerImController")
@RequestMapping("/mis/customer/im")
@RequiredArgsConstructor
public class CustomerImController {
    private final CustomerImService customerImService;

    @GetMapping("/searchServiceAccount")
    @SaCheckPermission(value = {"ROOT", "CUSTOMER_IM:SELECT"}, mode = SaMode.OR)
    public R searchServiceAccount() {
        HashMap result = customerImService.searchServiceAccount();
        return R.ok().put("result", result);
    }
}

