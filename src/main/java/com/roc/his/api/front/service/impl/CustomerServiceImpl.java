package com.roc.his.api.front.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.roc.his.api.db.dao.CustomerMapper;
import com.roc.his.api.db.dao.OrderMapper;
import com.roc.his.api.db.pojo.CustomerEntity;
import com.roc.his.api.front.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service("FrontCustomerService")
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final RedisTemplate redisTemplate;
    private final CustomerMapper customerMapper;
    private final OrderMapper orderMapper;

    @Override
    public boolean sendSmsCode(String tel) {
        //生成随机6位数字
        String code = RandomUtil.randomNumbers(6);
        System.out.println(code);

        String key = "sms_code_refresh_" + tel;
        //判断现在是否禁止向某个电话号码发送短信验证码
        if (redisTemplate.hasKey(key)) {
            return false;
        }

        //创建禁止重新发送短信验证码的缓存
        redisTemplate.opsForValue().set(key, code);
        redisTemplate.expire(key, 2, TimeUnit.MINUTES);

        //把数字和电话号码缓存到Redis，并且设置过期时间
        key = "sms_code_" + tel;
        redisTemplate.opsForValue().set(key, code);
        redisTemplate.expire(key, 5, TimeUnit.MINUTES);

        //TODO 调用短信接口，发送验证码短信

        return true;
    }

    @Override
    @Transactional
    public HashMap login(String tel, String code) {
        HashMap map = new HashMap();

        //核对短信验证码和电话号码
        String key = "sms_code_" + tel;
        if (!redisTemplate.hasKey(key)) {
            map.put("result", false);
            map.put("msg", "短信验证码已过期");
            return map;
        }

        String cacheCode = redisTemplate.opsForValue().get(key).toString();
        if (!cacheCode.equals(code)) {
            map.put("result", false);
            map.put("msg", "短信验证码不正确");
            return map;
        }

//        删除验证码缓存
        redisTemplate.delete(key);
        key = "sms_code_refresh_" + tel;
        redisTemplate.delete(key);

        Integer id = customerMapper.searchIdByTel(tel);
        //判断是不是新用户
        if (id == null) {
            CustomerEntity entity = new CustomerEntity();
            entity.setTel(tel);
            //注册新用户
            customerMapper.insert(entity);
            id = entity.getId();
        }

        map.put("id", id);
        map.put("result", true);
        map.put("msg", "登陆成功");
        return map;
    }

    @Override
    public HashMap searchSummary(int id) {
        HashMap map = customerMapper.searchById(id);
        map.putAll(orderMapper.searchFrontStatistic(id));
        return map;
    }

    @Override
    @Transactional
    public boolean update(Map param) {
        return customerMapper.update(param) == 1;
    }
}
