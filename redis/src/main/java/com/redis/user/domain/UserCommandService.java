package com.redis.user.domain;

import com.redis.common.CacheKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserCommandService {

    private final UserRepository userRepository;
    private final UserQueryService userQueryService;

    public UserCommandService(UserRepository userRepository, UserQueryService userQueryService) {
        this.userRepository = userRepository;
        this.userQueryService = userQueryService;
    }

    public User save(User user) {
        return this.userRepository.save(user);
    }

    @CacheEvict(value = CacheKey.USER, key = "#id")
    public void deleteById(Long id) {
        this.userRepository.deleteById(id);
    }

    @CachePut(value = CacheKey.USER, key = "#user.id")
    public User update(User user, String name, int age) {
        log.debug("AFTER DB SELECT");
        user.update(name, age);
        return user;
    }
}
