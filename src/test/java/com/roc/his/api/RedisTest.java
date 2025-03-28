package com.roc.his.api;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RedisTest {
    @Resource
    private RedisTemplate redisTemplate;

    @Test
    public void testRedisMulti() {
        redisTemplate.opsForValue().set("A", "1");

        Boolean execute = (Boolean) redisTemplate.execute(new SessionCallback() {
            @Override
            @SneakyThrows
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.watch("A");
                operations.multi();
                operations.opsForValue().set("A", "100");
                Thread.sleep(20000);
//                提交事务
//                set操作的结果为一个布尔类型的List
                List<Boolean> list = operations.exec();

                System.out.println(list);

                if (list.size() == 0) return false;

                return list.get(0);
            }
        });

        System.out.println("执行结果: " + execute);
    }
}
