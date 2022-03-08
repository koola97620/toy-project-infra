package com.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("h2")
public class RedisInputTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void redisTemplateMethodTest() {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set("key1", "key1Value");
        valueOperations.set("key2", "key2Value");

        String key1 = valueOperations.get("key1");

        assertThat(redisTemplate.type("key1")).isEqualTo(DataType.STRING);
        //assertThat(redisTemplate.countExistingKeys(List.of("key1", "key2", "key3"))).isEqualTo(2L);
        assertThat(redisTemplate.hasKey("key1")).isTrue();
        assertThat(redisTemplate.expireAt("key1", Date.from(LocalDateTime.now().plusDays(1L).atZone(ZoneId.systemDefault()).toInstant()))).isTrue();
        assertThat(redisTemplate.expire("key1", 60, TimeUnit.SECONDS)).isTrue();
        assertThat(redisTemplate.getExpire("key1")).isGreaterThan(0L);
        // key1 만료시간 해제
        assertThat(redisTemplate.persist("key1")).isTrue();
        // key1 만료시간이 세팅 안되어있는 경우 -1 반환
        assertThat(redisTemplate.getExpire("key1")).isEqualTo(-1L);
        assertThat(redisTemplate.delete("key1")).isTrue();
        assertThat(redisTemplate.delete(List.of("key1", "key2", "key3"))).isGreaterThan(0L);
    }

}
