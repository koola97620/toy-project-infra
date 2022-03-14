package com.redis.user.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateUserRequest {
    private String name;
    private int age;
}
