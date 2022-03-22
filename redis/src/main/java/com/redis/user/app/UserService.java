package com.redis.user.app;

import com.redis.common.CacheKey;
import com.redis.user.domain.User;
import com.redis.user.domain.UserRepository;
import com.redis.user.dto.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Cacheable(value = CacheKey.USER, key = "#id", unless = "#result == null")
    @Transactional(readOnly = true)
    public UserInfoResponse getUser(Long id) {
        User user = repository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        System.out.println("AFTER DB SELECT");
        return UserInfoResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .age(user.getAge())
                .build();
    }

    @Transactional
    public CreateUserResponse createUser(CreateUserRequest request) {
        User user = User.builder()
                .name(request.getName())
                .age(request.getAge())
                .build();
        User savedUser = repository.save(user);
        return CreateUserResponse.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .age(savedUser.getAge())
                .build();
    }

    @CachePut(value = CacheKey.USER, key = "#id")
    @Transactional
    public UserInfoResponse updateUser(Long id, UpdateUserRequest request) {
        User user = repository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        user.update(request.getName(), request.getAge());
        return UserInfoResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .age(user.getAge())
                .build();
    }

    @CacheEvict(value = CacheKey.USER, key = "#id")
    @Transactional
    public void deleteUser(Long id) {
        repository.deleteById(id);
    }
}
