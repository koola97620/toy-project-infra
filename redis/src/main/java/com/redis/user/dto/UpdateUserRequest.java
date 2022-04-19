package com.redis.user.dto;

import lombok.Builder;
import lombok.Getter;

public record UpdateUserRequest(String name, int age) {
    @Builder
    public UpdateUserRequest {}
}
