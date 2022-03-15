package com.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.*;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;


public class RedisInputTest extends IntegratedTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void commonCommand() {
        RedisTemplate<Object, Object> objectObjectRedisTemplate = new RedisTemplate<>();
        ClusterOperations<Object, Object> objectObjectClusterOperations = objectObjectRedisTemplate.opsForCluster();
        System.out.println(objectObjectClusterOperations);

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

    @Test
    void opsValue() {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        Collection<String> cacheKeys = new ArrayList<>();
        String cacheKey = "value_";
        for (int i = 0; i < 10; i++) {
            cacheKeys.add(cacheKey + 1);
            valueOperations.set(cacheKey + i, String.valueOf(i), 60, TimeUnit.SECONDS);
        }
        List<String> values = valueOperations.multiGet(cacheKeys);
        assertThat(values).isNotNull();
        assertThat(values.size()).isEqualTo(10);
        System.out.println(values.toString());
    }

    // List: 순서 있음, value 중복 허용
    @Test
    void opsList() {
        ListOperations<String, String> listOperations = redisTemplate.opsForList();
        String cacheKey = "valueList";
        for (int i = 0; i < 10; i++) {
            listOperations.leftPush(cacheKey, String.valueOf(i));
        }
        assertThat(redisTemplate.type(cacheKey)).isEqualTo(DataType.LIST);
        assertThat(listOperations.size(cacheKey)).isEqualTo(10L);
        assertThat(listOperations.range(cacheKey, 0, 10))
                .containsExactly("9", "8", "7", "6", "5", "4", "3", "2", "1", "0");

        assertThat(listOperations.rightPop(cacheKey)).isEqualTo("0");
        assertThat(listOperations.leftPop(cacheKey)).isEqualTo("9");
        assertThat(listOperations.size(cacheKey)).isEqualTo(8);

        assertThat(redisTemplate.delete(cacheKey)).isTrue();
        assertThat(listOperations.size(cacheKey)).isEqualTo(0);
    }

    // Hash : 순서 없음, key 중복 허용x, value 중복 허용
    @Test
    void opsHash() {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        String cacheKey = "valueHash";
        for (int i = 0; i < 10; i++) {
            hashOperations.put(cacheKey, "key_" + i, "value_" + i);
        }
        assertThat(redisTemplate.type(cacheKey)).isEqualTo(DataType.HASH);
        assertThat(hashOperations.size(cacheKey)).isEqualTo(10);
        Set<String> hkeys = hashOperations.keys(cacheKey);
        for (String hkey : hkeys) {
            System.out.println(hkey + " / " + hashOperations.get(cacheKey, hkey));
        }
        assertThat(hashOperations.get(cacheKey, "key_5")).isEqualTo("value_5");
        assertThat(hashOperations.delete(cacheKey, "key_5")).isEqualTo(1L);
        assertThat(hashOperations.get(cacheKey, "key_5")).isNull();
    }

    // Set : 순서 없음, value 중복 허용 안함
    @Test
    void opsSet() {
        SetOperations<String, String> setOperations = redisTemplate.opsForSet();
        String cacheKey = "valueSet";
        for (int i = 0; i < 10; i++) {
            setOperations.add(cacheKey, String.valueOf(i));
        }
        assertThat(redisTemplate.type(cacheKey)).isEqualTo(DataType.SET);
        assertThat(setOperations.members(cacheKey).size()).isEqualTo(10);

        redisTemplate.delete(cacheKey);

        for (int i = 0; i < 10; i++) {
            setOperations.add(cacheKey, String.valueOf(100));
        }
        assertThat(setOperations.members(cacheKey)).contains("100");
    }

    // SortedSet : 순서 있음, value 중복 허용 안함
    @Test
    void opsSortedSet() {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        String cacheKey = "valueZSet";
        for (int i = 0; i < 10; i++) {
            zSetOperations.add(cacheKey, String.valueOf(i), i);
        }

        assertThat(redisTemplate.type(cacheKey)).isEqualTo(DataType.ZSET);
        assertThat(zSetOperations.size(cacheKey)).isEqualTo(10);
        assertThat(zSetOperations.range(cacheKey, 0, 10))
                .containsExactly("0","1","2","3","4","5","6","7","8","9");
    }

}
