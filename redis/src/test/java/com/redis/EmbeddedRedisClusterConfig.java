package com.redis;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisCluster;
import redis.embedded.RedisExecProvider;
import redis.embedded.RedisServer;
import redis.embedded.RedisServerBuilder;
import redis.embedded.util.Architecture;
import redis.embedded.util.OS;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

@Profile("h2")
@Configuration
public class EmbeddedRedisClusterConfig {

    private RedisCluster redisCluster;

    @PostConstruct
    public void startRedisCluster() {
        List<Integer> group1 = List.of(42000, 52000);
        List<Integer> group2 = List.of(42001, 52001);
        List<Integer> group3 = List.of(42002, 52002);
        List<Integer> sentinelGroup = List.of(42003, 52003);
        RedisServerBuilder builder = RedisServer.builder()
                .port(32000).slaveOf("master1", 1)
                .setting("maxmemory 50M");// 윈도우에서만 설정 필요

        if (redisCluster == null || !redisCluster.isActive()) {
            System.out.println("RedisCluster Start");
            redisCluster = RedisCluster.builder()
                    //.ephemeral()
                    //.sentinelCount(3).quorumSize(2)
                    .withServerBuilder(builder)
                    .sentinelCount(0)
//                    .serverPorts(group1).replicationGroup("master1", 1)
//                    .serverPorts(group2).replicationGroup("master2", 1)
//                    .serverPorts(group3).replicationGroup("master3", 1)
                    //.serverPorts(sentinelGroup).replicationGroup("sentinel", 1)
                    //.ephemeralServers().replicationGroup("master3", 1)
                    .build();
        }
        redisCluster.start();
    }

    public List<Integer> getServerPorts() {
        return redisCluster.serverPorts();
    }

    @PreDestroy
    public void stopRedisCluster() {
        if (redisCluster != null) {
            redisCluster.stop();
        }
    }
}
