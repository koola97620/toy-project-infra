package com.redis.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.*;
import redis.embedded.util.Architecture;
import redis.embedded.util.OS;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.List;

//@Profile("h2")
//@Configuration
public class EmbeddedRedisClusterConfig {

    private RedisCluster redisCluster;

    @PostConstruct
    public void startRedisCluster() {
        RedisServerBuilder builder = RedisServer.builder()
                .setting("maxheap 1024M")// 윈도우에서만 설정 필요
                .setting("heapdir \"C:\\\\redis\"");// 윈도우에서만 설정 필요

        if (redisCluster == null || !redisCluster.isActive()) {
            redisCluster = RedisCluster.builder()
                    .withServerBuilder(builder)
                    .serverPorts(Arrays.asList(6300,6301,6302,6400,6401,6402))
                    .build();
        }
        redisCluster.start();
        System.out.println("Redis Start");
    }

    /*

    @PostConstruct
    public void startRedisCluster() {
//        RedisExecProvider.defaultProvider()
//                .override(OS.WINDOWS, Architecture.x86_64, "/path/to/windows/redis64");
        List<Integer> group1 = List.of(42000, 52000);
        List<Integer> group2 = List.of(42001, 52001);
        List<Integer> group3 = List.of(42002, 52002);
        List<Integer> sentinelGroup = List.of(42003, 52003);
        RedisServerBuilder builder = RedisServer.builder()
                .port(32000).slaveOf("master1", 1)
                //.setting("maxmemory 1024M");// 윈도우에서만 설정 필요
                .setting("maxheap 1024M")// 윈도우에서만 설정 필요
                .setting("heapdir \"C:\\\\redis\"");// 윈도우에서만 설정 필요

        if (redisCluster == null || !redisCluster.isActive()) {
            System.out.println("RedisCluster Start");
            redisCluster = RedisCluster.builder()
                    //.ephemeral()
                    //.sentinelCount(3).quorumSize(2)
                    .withServerBuilder(builder)
                    .sentinelCount(0)
                    .serverPorts(group1).replicationGroup("master1", 1)
                    .serverPorts(group2).replicationGroup("master2", 1)
                    .serverPorts(group3).replicationGroup("master3", 1)
                    //.serverPorts(sentinelGroup).replicationGroup("sentinel", 1)
                    //.ephemeralServers().replicationGroup("master3", 1)
                    .build();
        }
        redisCluster.start();
    }

     */
    public List<Integer> getServerPorts() {
        return redisCluster.serverPorts();
    }

    @PreDestroy
    public void stopRedisCluster() {
        if (redisCluster != null) {
            redisCluster.stop();
            System.out.println("Redis Stop");
        }
    }
}
