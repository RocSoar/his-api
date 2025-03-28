package com.roc.his.api.async;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateRange;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import com.roc.his.api.db.dao.AppointmentRestrictionMapper;
import com.roc.his.api.db.dao.SystemMapper;
import com.roc.his.api.db.pojo.SystemEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class InitializeWorkAsync {
    private final RedisTemplate redisTemplate;
    private final SystemMapper systemMapper;
    private final AppointmentRestrictionMapper appointmentRestrictionMapper;

    @Async("AsyncTaskExecutor")
    public void init() {
        //缓存全局设置
        this.loadSystemSetting();

        //生成未来60天的体检限流缓存
        this.createAppointmentCache();
    }

    private void loadSystemSetting() {
        List<SystemEntity> list = systemMapper.searchAll();
        list.forEach(one -> {
            redisTemplate.opsForValue().set("setting#" + one.getItem(), one.getValue());
        });
        log.info("系统设置缓存成功");
    }

    private void createAppointmentCache() {
        DateTime startDate = DateUtil.tomorrow();
        DateTime endDate = startDate.offsetNew(DateField.DAY_OF_MONTH, 60);
        DateRange range = DateUtil.range(startDate, endDate, DateField.DAY_OF_MONTH);

        Map param = Map.of("startDate", startDate.toDateStr(), "endDate", endDate.toDateStr());

//        查询限流表限流记录
        List<HashMap> list = appointmentRestrictionMapper.searchScheduleInRange(param);

        range.forEach(one -> {
            String date = one.toDateStr();
            int maxNum = Integer.parseInt(redisTemplate.opsForValue().get("setting#appointment_number").toString());
            int realNum = 0;
            for (HashMap map : list) {
                String temp = MapUtil.getStr(map, "date");
//                如果限流表存在当天限流记录, 就使用限流表中的数据
                if (date.equals(temp)) {
                    maxNum = MapUtil.getInt(map, "num_1");
                    realNum = MapUtil.getInt(map, "num_3");
                    break;
                }
            }
            //设置未来60天限流缓存
            HashMap cache = new HashMap();
            cache.put("maxNum", maxNum);
            cache.put("realNum", realNum);
            String key = "appointment#" + date;
            redisTemplate.opsForHash().putAll(key, cache);
//           当天过完后 当天缓存就过期
            DateTime dateTime = new DateTime(date).offsetNew(DateField.DAY_OF_MONTH, 1);
            redisTemplate.expireAt(key, dateTime);
        });

        log.info("未来60天体检人数缓存成功");
    }
}

