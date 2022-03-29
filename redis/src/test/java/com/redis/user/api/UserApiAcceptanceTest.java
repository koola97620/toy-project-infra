package com.redis.user.api;

import com.redis.IntegratedTest;
import com.redis.user.domain.User;
import com.redis.user.dto.CreateUserRequest;
import com.redis.user.dto.CreateUserResponse;
import com.redis.user.dto.UpdateUserRequest;
import com.redis.user.dto.UserInfoResponse;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class UserApiAcceptanceTest extends IntegratedTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @DisplayName("UserApi Acceptance Test")
    @Test
    void acceptanceTest() {
        // 유저 정보 저장 api 호출
        // 유저 정보 조회 api 호출
        // Redis 조회
        // 유저 정보 변경 api 호출
        // 유저 정보 조회 api 호출
        // Redis 조회
        // 유저 정보 삭제 api 호출
        // 유저 정보 조회 api 호출
        // Redis 조회
        CreateUserRequest request = CreateUserRequest.builder()
                .name("jdragon")
                .age(10)
                .build();

        ExtractableResponse<Response> userRegistApiResponse = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/user")
                .then().log().all().extract();

        assertThat(userRegistApiResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        CreateUserResponse createdUser = userRegistApiResponse.as(CreateUserResponse.class);

        ExtractableResponse<Response> selectUserApiResponse = RestAssured
                .given().log().all()
                .pathParam("userId", createdUser.getId())
                .when().get("/user/{userId}")
                .then().log().all()
                .extract();
        assertThat(selectUserApiResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        UserInfoResponse selectResponse1 = selectUserApiResponse.as(UserInfoResponse.class);

        Set keys = redisTemplate.keys("user::" + createdUser.getId());
        assertThat(keys.size()).isEqualTo(1);
        ValueOperations valueOperations = redisTemplate.opsForValue();
        User cacheUserResponse = (User) valueOperations.get("user::" + createdUser.getId());
        assertThat(cacheUserResponse.getId()).isEqualTo(selectResponse1.getId());
        assertThat(cacheUserResponse.getName()).isEqualTo(selectResponse1.getName());
        assertThat(cacheUserResponse.getAge()).isEqualTo(selectResponse1.getAge());

        UpdateUserRequest updateRequest = UpdateUserRequest.builder()
                .name("newJdragon")
                .age(20)
                .build();

        ExtractableResponse<Response> updateApiResponse = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(updateRequest)
                .pathParam("userId", createdUser.getId())
                .when().put("/users/{userId}")
                .then().log().all()
                .extract();
        assertThat(updateApiResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        UserInfoResponse updateUserResponse = updateApiResponse.as(UserInfoResponse.class);


        ExtractableResponse<Response> selectUserApiResponse2 = RestAssured
                .given().log().all()
                .pathParam("userId", updateUserResponse.getId())
                .when().get("/user/{userId}")
                .then().log().all()
                .extract();
        assertThat(selectUserApiResponse2.statusCode()).isEqualTo(HttpStatus.OK.value());
        UserInfoResponse selectResponse2 = selectUserApiResponse2.as(UserInfoResponse.class);

        Set redisKeys = redisTemplate.keys("user::" + updateUserResponse.getId());
        assertThat(redisKeys.size()).isEqualTo(1);
        ValueOperations valueOperations2 = redisTemplate.opsForValue();
        User cacheUserResponse2 = (User) valueOperations2.get("user::" + updateUserResponse.getId());
        assertThat(cacheUserResponse2.getId()).isEqualTo(selectResponse2.getId());
        assertThat(cacheUserResponse2.getName()).isEqualTo(selectResponse2.getName());
        assertThat(cacheUserResponse2.getAge()).isEqualTo(selectResponse2.getAge());


        ExtractableResponse<Response> deleteResponse = RestAssured
                .given().log().all()
                .pathParam("userId", createdUser.getId())
                .when().delete("/users/{userId}")
                .then().log().all()
                .extract();
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        Set redisKeys3 = redisTemplate.keys("user::" + createdUser.getId());
        assertThat(redisKeys3.size()).isEqualTo(0);
    }
}