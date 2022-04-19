package com.redis.user.app;

import com.redis.user.domain.User;
import com.redis.user.domain.UserCommandService;
import com.redis.user.domain.UserQueryService;
import com.redis.user.dto.CreateUserRequest;
import com.redis.user.dto.CreateUserResponse;
import com.redis.user.dto.UpdateUserRequest;
import com.redis.user.dto.UserInfoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserService {

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;

    public UserService(UserQueryService userQueryService, UserCommandService userCommandService) {
        this.userQueryService = userQueryService;
        this.userCommandService = userCommandService;
    }

    @Transactional(readOnly = true)
    public UserInfoResponse getUser(Long id) {
        User user = userQueryService.findById(id);
        log.debug("AFTER DB SELECT");
        return UserInfoResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .age(user.getAge())
                .build();
    }

    @Transactional
    public CreateUserResponse createUser(CreateUserRequest request) {
        User user = User.builder()
                .name(request.name())
                .age(request.age())
                .build();
        User savedUser = userCommandService.save(user);
        return CreateUserResponse.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .age(savedUser.getAge())
                .build();
    }


    @Transactional
    public UserInfoResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userQueryService.findById(id);
        User updatedUser = userCommandService.update(user, request.name(), request.age());
        return UserInfoResponse.builder()
                .id(updatedUser.getId())
                .name(updatedUser.getName())
                .age(updatedUser.getAge())
                .build();
    }

    @Transactional
    public void deleteUser(Long id) {
        userCommandService.deleteById(id);
    }
}
