package com.redis.common;

import io.lettuce.core.ReadFrom;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Profile("!h2")
@Configuration
@EnableCaching
public class RedisCacheConfig {

    @Autowired
    private ClusterConfigurationProperties clusterConfigurationProperties;

    @Bean
    public RedisConnectionFactory connectionFactory() {
//        ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
//                .enablePeriodicRefresh()
//                .enableAllAdaptiveRefreshTriggers()
//                .build();
//
//        ClusterClientOptions options = ClusterClientOptions.builder()
//                .pingBeforeActivateConnection(true)
//                .validateClusterNodeMembership(false)
//                .topologyRefreshOptions(clusterTopologyRefreshOptions)
//                .timeoutOptions(TimeoutOptions.enabled(Duration.ofSeconds(5)))
//                .build();
//
//        LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder()
//                .readFrom(ReadFrom.REPLICA)
//                .clientOptions(options)
//                .build();
//
//        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(clusterConfigurationProperties.getNodes());
//
//        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory();

        return new LettuceConnectionFactory(
                new RedisClusterConfiguration(clusterConfigurationProperties.getNodes())
                //new RedisClusterConfiguration(clusterConfigurationProperties.getNodes()), lettuceClientConfiguration
        );
    }

//    @Value("${spring.redis.cluster.nodes}")
//    private List<String> clusterNodes;

//    @Bean
//    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
//        RedisTemplate redisTemplate = new RedisTemplate();
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//        redisTemplate.setConnectionFactory(redisConnectionFactory);
//        return redisTemplate;
//    }

//    @Bean(destroyMethod = "shutdown")
//    public RedisClusterClient redisClusterClient() {
//        ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
//                .enablePeriodicRefresh()
//                .enableAllAdaptiveRefreshTriggers()
//                .build();
//
//        ClusterClientOptions options = ClusterClientOptions.builder()
//                .pingBeforeActivateConnection(true)
//                .validateClusterNodeMembership(false)
//                .topologyRefreshOptions(clusterTopologyRefreshOptions)
//                .timeoutOptions(TimeoutOptions.enabled(Duration.ofSeconds(5)))
//                .build();
//        List<RedisURI> redisUris = new ArrayList<>();
//        for (String node : clusterNodes) {
//            String[] split = node.split(":");
//            redisUris.add(RedisURI.builder().withHost(split[0]).withPort(Integer.parseInt(split[1])).build());
//        }
//
//        RedisClusterClient redisClusterClient = RedisClusterClient.create(redisUris);
//        redisClusterClient.setOptions(options);
//        redisClusterClient.setDefaultTimeout(Duration.ofSeconds(5));
//        return redisClusterClient;
//    }

//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(clusterNodes);
//
//        Set<RedisNode> set = new HashSet<>();
//        clusterNodes.forEach(node -> {
//            String[] split = node.split(":");
//            set.add(new RedisNode(split[0], Integer.parseInt(split[1])));
//            //redisClusterConfiguration.clusterNode(split[0], Integer.parseInt(split[1]));
//        });
//
//        redisClusterConfiguration.setClusterNodes(set);
//        return new LettuceConnectionFactory(redisClusterConfiguration);
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
