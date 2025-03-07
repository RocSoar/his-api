package com.roc.his.api.controller;

import com.roc.his.api.common.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/test")
@RestController
public class Test {

    @GetMapping("/demo")
    public R demo() {
        return R.ok("执行成功!");
    }
}
