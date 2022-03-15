package com.redis.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateUserResponse {
    private Long id;
    private String name;
    private int age;
}
