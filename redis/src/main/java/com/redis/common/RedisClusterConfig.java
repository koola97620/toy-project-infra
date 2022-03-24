package com.redis.common;

import io.lettuce.core.ReadFrom;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.cluster.RedisClusterClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClusterConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionProvider;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

@Profile("dev")
@EnableCaching
@Configuration
public class RedisClusterConfig {

    @Value("${spring.redis.cluster.master.nodes}")
    private String masterUris;

    @Value("${spring.redis.cluster.slave.nodes}")
    private String slaveUris;

//    private RedisInstance master;
//    private List<RedisInstance> slaves;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder()
                //.readFrom(ReadFrom.REPLICA_PREFERRED)
                .readFrom(ReadFrom.REPLICA)
                .build();

        String[] splitMasterUris = masterUris.split(",");
        String[] splitSlaveUris = slaveUris.split(",");
        RedisClusterConfiguration dd = new RedisClusterConfiguration();
        RedisClusterConfiguration abc = new RedisClusterConfiguration();
        for (int i=0 ; i < splitMasterUris.length ; i++) {
            String[] masterHostAndPort = splitMasterUris[i].split(":");
            String[] slaveHostAndPort = splitSlaveUris[i].split(":");
//            RedisStaticMasterReplicaConfiguration staticMasterReplicaConfiguration = new RedisStaticMasterReplicaConfiguration(masterHostAndPort[0],Integer.parseInt(masterHostAndPort[1]));
//            staticMasterReplicaConfiguration.addNode(slaveHostAndPort[0], Integer.parseInt(slaveHostAndPort[1]));

            //abc.clusterNode(masterHostAndPort[0], Integer.parseInt(masterHostAndPort[1]));
            abc.addClusterNode(RedisClusterNode.newRedisClusterNode().listeningAt(masterHostAndPort[0], Integer.parseInt(masterHostAndPort[1])).build());
            abc.addClusterNode(RedisClusterNode.newRedisClusterNode().listeningAt(slaveHostAndPort[0], Integer.parseInt(slaveHostAndPort[1])).build());



//            RedisClusterNode redisClusterNode = RedisClusterNode.newRedisClusterNode()
//                    .listeningAt(masterHostAndPort[0], Integer.parseInt(masterHostAndPort[1]))
//                    .promotedAs(RedisNode.NodeType.MASTER)
//                    .withId("master"+i)
//                    .build();
//
//            RedisClusterNode redisClusterNode2 = RedisClusterNode.newRedisClusterNode()
//                    .listeningAt(slaveHostAndPort[0], Integer.parseInt(slaveHostAndPort[1]))
//                    .promotedAs(RedisNode.NodeType.SLAVE)
//                    .slaveOf("master"+i)
//                    .build();
//
//            dd.addClusterNode(redisClusterNode);
//            dd.addClusterNode(redisClusterNode2);
        }
        return new LettuceConnectionFactory(abc, clientConfiguration);

//        RedisClusterClient redisClusterClient = RedisClusterClient.create(List.of(
//                RedisURI.create("")
//        ));
//        LettuceClusterConnection lettuceClusterConnection = new LettuceClusterConnection(redisClusterClient);

        //return new LettuceConnectionFactory(staticMasterReplicaConfiguration, clientConfiguration);
        //return new LettuceConnectionFactory(dd,clientConfiguration);
    }


//    public static class RedisInstance {
//        private String host;
//        private int port;
//
//        public RedisInstance(String url) {
//            String[] split = url.split(":");
//            this.host = split[0];
//            this.port = Integer.parseInt(split[1]);
//        }
//
//    }










    @Bean(name = "cacheManager")
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        System.out.println(connectionFactory.getClass());
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .entryTtl(Duration.ofSeconds(CacheKey.DEFAULT_EXPIRE_SEC))
                .computePrefixWith(CacheKeyPrefix.simple())
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                );
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put(
                CacheKey.USER,
                RedisCacheConfiguration.defaultCacheConfig()
                        .serializeKeysWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                        )
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())
                        )
                        .entryTtl(Duration.ofSeconds(CacheKey.USER_EXPIRE_SEC))
        );

        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(connectionFactory)
                .cacheDefaults(configuration)
                .withInitialCacheConfigurations(cacheConfigurations).build();
    }
}
