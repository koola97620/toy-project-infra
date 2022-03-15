package com.redis.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateUserRequest {
    private String name;
    private int age;
}
