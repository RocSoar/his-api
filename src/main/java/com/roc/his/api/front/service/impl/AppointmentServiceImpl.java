package com.roc.his.api.front.service.impl;

import cn.hutool.core.map.MapUtil;
import com.roc.his.api.common.PageUtils;
import com.roc.his.api.db.dao.AppointmentMapper;
import com.roc.his.api.db.dao.AppointmentRestrictionMapper;
import com.roc.his.api.db.dao.OrderMapper;
import com.roc.his.api.db.pojo.AppointmentEntity;
import com.roc.his.api.front.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("FrontAppointmentService")
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentMapper appointmentMapper;
    private final AppointmentRestrictionMapper appointmentRestrictionMapper;
    private final OrderMapper orderMapper;
    private final RedisTemplate redisTemplate;

    @Override
    @Transactional
    public String insert(AppointmentEntity entity) {
        Map<String, String> resultCode = Map.of(
                "full", "当天预约已满，请选择其他日期",
                "fail", "预约失败",
                "success", "预约成功");

        String key = "appointment#" + entity.getDate();

//        向redis中缓存预约人数, 使用redis事务机制防止并发导致的超售现象
        String execute = (String) redisTemplate.execute(new SessionCallback<>() {
            @Override
            public String execute(RedisOperations operations) throws DataAccessException {
                //关注缓存数据（拿到乐观锁的Version）(监控原始数据)
                operations.watch(key);

                //拿到缓存的数据
                Map entry = operations.opsForHash().entries(key);
                int maxNum = Integer.parseInt(entry.get("maxNum").toString());
                int realNum = Integer.parseInt(entry.get("realNum").toString());

                if (realNum >= maxNum) {
                    //当天预约人数已满
                    operations.unwatch();
                    return resultCode.get("full");
                }

                //开启Redis事务
                operations.multi();
                //已预约人数+1
                operations.opsForHash().increment(key, "realNum", 1);

                //提交事务
//                    increment操作的结果为一个Long类型的List, 结果为realNum + 1后的值
                List<Long> list = operations.exec();
//                    如果size为0, 则是因为并发问题导致的失败(原始数据已被修改, 事务提交被拒绝)
                if (list.size() == 0) {
                    return resultCode.get("fail");
                }
                long num = list.get(0);
//                    如果num为0, 则可能是key不存在导致的失败
                return resultCode.get(num > 0 ? "success" : "fail");
            }
        });

        //如果Redis事务提交失败 或预约人数已满 就结束Service方法
        if (!execute.equals(resultCode.get("success"))) {
            return execute;
        }

        //向体检预约表中添加一条记录
        int rows = appointmentMapper.insert(entity);
        if (rows != 1) {
            return resultCode.get("fail");
        }

        Map entry = redisTemplate.opsForHash().entries(key);
        int maxNum = Integer.parseInt(entry.get("maxNum").toString());

        HashMap param = new HashMap();
        param.put("date", entity.getDate());
        param.put("num_1", maxNum);
        param.put("num_2", maxNum);
        param.put("num_3", 1);

        //更新预约限流表中的预约人数
        rows = appointmentRestrictionMapper.saveOrUpdateRealNum(param);
        if (rows == 0) {
            return resultCode.get("fail");
        }

        //更新订单状态为已预约
        int orderId = entity.getOrderId();
        rows = orderMapper.updateStatus(Map.of("status", 5, "id", orderId));
        if (rows == 0) {
            return resultCode.get("fail");
        }

        return resultCode.get("success");
    }

    @Override
    public PageUtils searchByPage(Map param) {
        List<HashMap> list = new ArrayList<>();
        long count = appointmentMapper.searchFrontAppointmentCount(param);
        if (count > 0) {
            list = appointmentMapper.searchFrontAppointmentByPage(param);
        }
        int page = MapUtil.getInt(param, "page");
        int length = MapUtil.getInt(param, "length");
        PageUtils pageUtils = new PageUtils(list, count, page, length);
        return pageUtils;
    }
}

