package com.roc.his.api.mis.service.impl;

import com.roc.his.api.mis.service.CustomerImService;
import com.tencentyun.TLSSigAPIv2;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service("MisCustomerImService")
@RequiredArgsConstructor
public class CustomerImServiceImpl implements CustomerImService {
    @Value("${tencent.im.sdkAppId}")
    private Long sdkAppId;

    @Value("${tencent.im.secretKey}")
    private String secretKey;

    @Value("${tencent.im.customerServiceId}")
    private String customerServiceId;

    @Override
    public HashMap searchServiceAccount() {
        TLSSigAPIv2 api = new TLSSigAPIv2(sdkAppId, secretKey);
        //生成客服账号签名
        String userSig = api.genUserSig(customerServiceId, 180 * 86400);
        //保存返回的结果
        HashMap result = new HashMap();
        result.put("sdkAppId", sdkAppId);
        result.put("account", customerServiceId);
        result.put("userSig", userSig);
        return result;
    }

}

