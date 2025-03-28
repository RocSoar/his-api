package com.roc.his.api.front.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.roc.his.api.db.dao.CustomerImMapper;
import com.roc.his.api.db.dao.CustomerMapper;
import com.roc.his.api.exception.HisException;
import com.roc.his.api.front.service.CustomerImService;
import com.tencentyun.TLSSigAPIv2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("FrontCustomerImService")
@Slf4j
@RequiredArgsConstructor
public class CustomerImServiceImpl implements CustomerImService {
    @Value("${tencent.im.sdkAppId}")
    private Long sdkAppId;

    @Value("${tencent.im.secretKey}")
    private String secretKey;

    @Value("${tencent.im.managerId}")
    private String managerId;

    @Value("${tencent.im.customerServiceId}")
    private String customerServiceId;

    @Value("${tencent.im.baseUrl}")
    private String baseUrl;

    private final CustomerMapper customerMapper;
    private final CustomerImMapper customerImMapper;


    @Override
    @Transactional
    public HashMap createAccount(int customerId) {
        HashMap map = customerMapper.searchById(customerId);

        String tel = MapUtil.getStr(map, "tel");
        String photo = MapUtil.getStr(map, "photo");
        String customerAccount = "customer_" + customerId;
        String nickname = "客户_" + tel;

        TLSSigAPIv2 api = new TLSSigAPIv2(sdkAppId, secretKey);
        String customerSig = api.genUserSig(customerAccount, 180 * 86400); //生成客户账号签名

        //保存返回的结果
        HashMap result = new HashMap();
        result.put("sdkAppId", sdkAppId);
        result.put("account", customerAccount);
        result.put("userSig", customerSig);

        String administratorSig = api.genUserSig(managerId, 180 * 86400);//生成管理员账号签名

        //查询是否创建了客户IM账号
        String url = baseUrl + "v4/im_open_login_svc/account_check?sdkappid=" +
                sdkAppId + "&identifier=" + managerId + "&usersig=" + administratorSig +
                "&random=" + RandomUtil.randomInt(1, 99999999) + "&contenttype=json";

        JSONObject json = new JSONObject();
        json.set("CheckItem", List.of(Map.of("UserID", customerAccount)));

        String response = HttpUtil.post(url, json.toString());

        JSONObject entries = JSONUtil.parseObj(response);
        Integer errorCode = entries.getInt("ErrorCode");
        String errorInfo = entries.getStr("ErrorInfo");

        if (errorCode != 0) {
            log.error("查询客户IM账号失败：" + errorInfo);
            throw new HisException("客服系统异常");
        }
        JSONArray list = (JSONArray) entries.get("ResultItem");
        JSONObject object = (JSONObject) list.get(0);
        String accountStatus = object.getStr("AccountStatus");

        //如果存在该客户IM账号
        if ("Imported".equals(accountStatus)) {
            //更新客户IM账号登陆时间
            int rows = customerImMapper.insert(customerId);
            if (rows == 0) {
                log.error("无法更新客户IM账号登陆时间，客户账号ID：" + customerId);
                throw new HisException("客服系统异常");
            }
            //发送欢迎词
            this.sendWelcomeMessage(customerAccount);
            return result;
        }

        //创建客户IM账号
        url = baseUrl + "v4/im_open_login_svc/account_import?sdkappid=" +
                sdkAppId + "&identifier=" + managerId + "&usersig=" +
                administratorSig + "&random=" + RandomUtil.randomInt(1, 99999999) +
                "&contenttype=json";

        json = new JSONObject();
        json.set("UserID", customerAccount);
        json.set("Nick", nickname);
        if (photo != null) {
            json.set("FaceUrl", photo);
        }

        response = HttpUtil.post(url, json.toString());

        entries = JSONUtil.parseObj(response);
        errorCode = entries.getInt("ErrorCode");
        errorInfo = entries.getStr("ErrorInfo");
        if (errorCode != 0) {
            log.error("创建客户IM账号失败：" + errorInfo);
            throw new HisException("客服系统异常");
        }

        //给客户IM账号添加客服好友
        url = baseUrl + "v4/sns/friend_add?sdkappid=" + sdkAppId +
                "&identifier=" + managerId + "&usersig=" + administratorSig +
                "&random=" + RandomUtil.randomInt(1, 99999999) +
                "&contenttype=json";
        json = new JSONObject();
        json.set("From_Account", customerAccount);
        json.set("AddFriendItem", List.of(Map.of("To_Account", customerServiceId, "AddSource", "AddSource_Type_Web")));

        response = HttpUtil.post(url, json.toString());
        entries = JSONUtil.parseObj(response);
        errorCode = entries.getInt("ErrorCode");
        errorInfo = entries.getStr("ErrorInfo");
        if (errorCode != 0) {
            log.error("添加客服IM好友失败:" + errorInfo);
            throw new HisException("客服系统异常");
        }

        list = (JSONArray) entries.get("ResultItem");
        object = (JSONObject) list.get(0);

        int resultCode = object.getInt("ResultCode");
        String resultInfo = object.getStr("ResultInfo");
        if (resultCode != 0) {
            log.error("添加客服IM好友失败：" + resultInfo);
            throw new HisException("客服系统异常");
        }

        //更新客户IM账号登陆时间
        int rows = customerImMapper.insert(customerId);
        if (rows == 0) {
            log.error("无法更新客户IM账号登陆记录，客户账号ID：" + customerId);
            throw new HisException("客服系统异常");
        }
        //发送欢迎词
        this.sendWelcomeMessage(customerAccount);
        return result;
    }

    private void sendWelcomeMessage(String account) {
        TLSSigAPIv2 api = new TLSSigAPIv2(sdkAppId, secretKey);
        //生成客服账号签名
        String userSig = api.genUserSig(customerServiceId, 180 * 86400);
        String url = baseUrl + "v4/openim/sendmsg?sdkappid=" + sdkAppId +
                "&identifier=" + customerServiceId + "&usersig=" + userSig +
                "&random=" + RandomUtil.randomInt(1, 99999999) + "&contenttype=json";

        JSONObject json = new JSONObject();
        json.set("SyncOtherMachine", 2); //欢迎词消息不同步至发送方
        json.set("To_Account", account);
        json.set("MsgLifeTime", 120); //欢迎词 消息保存两分钟
        json.set("MsgRandom", RandomUtil.randomInt(1, 99999999)); //用于消息去重
        json.set("MsgBody", List.of(Map.of("MsgType", "TIMTextElem",
                "MsgContent", Map.of("Text", "(测试)亲，您好，非常高兴为您服务，有什么可以为您效劳的呢?")
        )));

        String response = HttpUtil.post(url, json.toString());
        JSONObject entries = JSONUtil.parseObj(response);
        int errorCode = entries.getInt("ErrorCode");
        String errorInfo = entries.getStr("ErrorInfo");
        if (errorCode != 0) {
            log.error("发送欢迎词失败：" + errorInfo.toString());
            throw new HisException("客服系统异常");
        }
    }
}

