package com.redis.user.dto;


import lombok.Builder;
import lombok.Getter;

public record CreateUserRequest(String name, int age) {
    @Builder
    public CreateUserRequest {}
}
