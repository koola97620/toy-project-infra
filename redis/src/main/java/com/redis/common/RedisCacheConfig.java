package com.redis.common;

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
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Profile("local")
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
//                .readFrom(ReadFrom.REPLICA_PREFERRED)
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


//    @Bean
//    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
//        RedisTemplate redisTemplate = new RedisTemplate();
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//        redisTemplate.setConnectionFactory(redisConnectionFactory);
//        return redisTemplate;
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
