package com.roc.his.api.schedule;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class AppointmentSchedule {
    private final RedisTemplate redisTemplate;

    @Scheduled(cron = "0 50 23 * * ?")
    public void createCacheAfter62Day() {
        int maxNum = Integer.parseInt(redisTemplate.opsForValue().get("setting#appointment_number").toString());
        int realNum = 0;
        String date = new DateTime().offset(DateField.DAY_OF_MONTH, 62).toDateStr();
        String key = "appointment#" + date;
        redisTemplate.opsForHash().putAll(key, Map.of("maxNum", maxNum, "realNum", realNum));

        DateTime dateTime = new DateTime(date).offsetNew(DateField.DAY_OF_MONTH, 1);
        redisTemplate.expireAt(key, dateTime);
        log.info("生成了" + date + "的体检限流缓存");
    }
}

