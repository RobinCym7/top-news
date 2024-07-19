package com.cym.schedule;

import com.cym.common.redis.CacheService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTest {

    @Autowired
    private CacheService cacheService;

    @Test
    public void testList(){
        // 在list的左边添加元素
        // cacheService.lLeftPush("list_001", "hello, redis");

        // 在list右边获取元素，并删除
        String list_001 = cacheService.lRightPop("list_001");
        System.out.println(list_001);
    }

    @Test
    public void testZset(){
        // 添加数据到zset中，最后一个参数为分值
        // cacheService.zAdd("zset_key_001", "hello_zset_001", 100);
        // cacheService.zAdd("zset_key_001", "hello_zset_002", 300);
        // cacheService.zAdd("zset_key_001", "hello_zset_003", 200);
        // cacheService.zAdd("zset_key_001", "hello_zset_004", 400);

        // 获取zset中的数据
        Set<String> zset_key_001 = cacheService.zRangeByScore("zset_key_001", 0, 350);
        System.out.println(zset_key_001);
    }

}
