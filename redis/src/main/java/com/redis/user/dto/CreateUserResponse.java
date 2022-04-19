package com.redis.user.dto;

import lombok.Builder;
import lombok.Getter;

public record CreateUserResponse(Long id, String name, int age) {
    @Builder
    public CreateUserResponse {}
}
