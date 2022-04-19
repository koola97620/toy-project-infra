package com.redis.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public record UserInfoResponse(Long id, String name, int age) {
    @Builder
    public UserInfoResponse {}
}
