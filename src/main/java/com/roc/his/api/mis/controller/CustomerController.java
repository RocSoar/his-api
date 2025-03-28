package com.roc.his.api.mis.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.roc.his.api.common.R;
import com.roc.his.api.mis.dto.SearchCustomerSummaryDTO;
import com.roc.his.api.mis.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;

@RestController("MisCustomerController")
@RequestMapping("/mis/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping("/searchSummary")
    @SaCheckLogin
    public R searchSummary(@RequestBody @Valid SearchCustomerSummaryDTO dto) {
        HashMap map = customerService.searchSummary(dto.getCustomerId());
        return R.ok().put("result", map);
    }
}

