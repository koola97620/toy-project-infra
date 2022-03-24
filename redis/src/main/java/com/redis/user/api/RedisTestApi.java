package com.redis.user.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClusterConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class RedisTestApi {
    @Autowired
    private LettuceConnectionFactory lettuceConnectionFactory;

    @GetMapping("/test/redis/")
    public String test() {
        RedisClusterConfiguration clusterConfiguration = lettuceConnectionFactory.getClusterConfiguration();
        Set<RedisNode> clusterNodes = clusterConfiguration.getClusterNodes();

        for(RedisNode node : clusterNodes) {
            System.out.println(node.getHost() + ":" + node.getPort());
            System.out.println(node.isMaster());
            System.out.println(node.isReplica());
            System.out.println(node.isSlave());
            System.out.println(node.getId());
            System.out.println(node.getMasterId());
        }

        return clusterNodes.stream()
                .map(node -> node.getHost() + ":" + node.getPort())
                .collect(Collectors.joining(","));
    }
}
