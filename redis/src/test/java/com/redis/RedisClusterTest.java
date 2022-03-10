//package com.redis;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.List;
//
//@SpringBootTest
//@ActiveProfiles("h2")
//public class RedisClusterTest {
//
//    @Autowired
//    private EmbeddedRedisClusterConfig config;
//
//    @Test
//    void test() {
//        List<Integer> serverPorts = config.getServerPorts();
//        System.out.println(serverPorts.toString());
//    }
//
//}
