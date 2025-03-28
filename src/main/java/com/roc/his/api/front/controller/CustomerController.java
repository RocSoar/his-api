package com.roc.his.api.front.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import com.roc.his.api.common.R;
import com.roc.his.api.config.sa_token.StpCustomerUtil;
import com.roc.his.api.front.dto.LoginDTO;
import com.roc.his.api.front.dto.SendSmsCodeDTO;
import com.roc.his.api.front.dto.UpdateCustomerDTO;
import com.roc.his.api.front.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController("FrontCustomerController")
@RequestMapping("/front/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping("/sendSmsCode")
    public R sendSmsCode(@RequestBody @Valid SendSmsCodeDTO dto) {
        boolean bool = customerService.sendSmsCode(dto.getTel());
        String msg = bool ? "短信验证码已发送" : "无法发送短信验证码";
        return R.ok(msg).put("result", bool);
    }

    @PostMapping("/login")
    public R login(@RequestBody @Valid LoginDTO dto) {
        HashMap map = customerService.login(dto.getTel(), dto.getCode());
        boolean result = MapUtil.getBool(map, "result");
        String msg = MapUtil.getStr(map, "msg");
        R r = R.ok(msg).put("result", result);
        if (result) {
            //生成令牌
            int id = MapUtil.getInt(map, "id");
            StpCustomerUtil.login(id, "PC");
            String token = StpCustomerUtil.getTokenValue();
            r.put("token", token);
        }
        return r;
    }

    @GetMapping("/logout")
    @SaCheckLogin(type = StpCustomerUtil.TYPE)
    public R logout() {
        int id = StpCustomerUtil.getLoginIdAsInt();
        StpCustomerUtil.logout(id, "PC");
        return R.ok();
    }

    @GetMapping("/checkLogin")
    public R checkLogin() {
        boolean bool = StpCustomerUtil.isLogin();
        return R.ok().put("result", bool);
    }

    @GetMapping("/searchSummary")
    @SaCheckLogin(type = StpCustomerUtil.TYPE)
    public R searchSummary() {
        int id = StpCustomerUtil.getLoginIdAsInt();
        HashMap map = customerService.searchSummary(id);
        return R.ok().put("result", map);
    }

    @PostMapping("/update")
    @SaCheckLogin(type = StpCustomerUtil.TYPE)
    public R update(@RequestBody @Valid UpdateCustomerDTO dto) {
        int id = StpCustomerUtil.getLoginIdAsInt();
        Map<String, Object> param = BeanUtil.beanToMap(dto);
        param.put("id", id);
        boolean bool = customerService.update(param);
        return R.ok().put("result", bool);
    }
}

