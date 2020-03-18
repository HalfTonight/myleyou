package com.leyou.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

public class RedisTest {
    @Autowired
    StringRedisTemplate template;
    @Test
    public void insert(){

    }
}
