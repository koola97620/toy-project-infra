package com.redis.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateUserResponse {
    private Long id;
    private String name;
    private int age;
}
