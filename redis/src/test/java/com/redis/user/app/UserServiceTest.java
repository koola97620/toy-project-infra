package com.redis.user.app;

import com.redis.IntegratedTest;
import com.redis.user.dto.CreateUserRequest;
import com.redis.user.dto.CreateUserResponse;
import com.redis.user.dto.UserInfoResponse;
import com.redis.util.DatabaseCleanup;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class UserServiceTest extends IntegratedTest {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    private CreateUserResponse savedUser;

    @BeforeEach
    public void setUp() {
        CreateUserRequest createUserRequest =
                CreateUserRequest.builder()
                        .name("jdragon")
                        .age(10)
                        .build();
        savedUser = userService.createUser(createUserRequest);
    }

    @DisplayName("저장된 User 조회")
    @Test
    void save() {
        UserInfoResponse dbUser = userService.getUser(savedUser.getId());
        assertThat(dbUser.getId()).isEqualTo(savedUser.getId());
        assertThat(dbUser.getName()).isEqualTo(savedUser.getName());
    }

    @DisplayName("캐시에 저장된 User 조회")
    @Test
    void cacheSaveUser() {
        UserInfoResponse dbUser = userService.getUser(savedUser.getId());
        assertThat(dbUser.getId()).isEqualTo(savedUser.getId());
        assertThat(dbUser.getName()).isEqualTo(savedUser.getName());

        repetitiveCall(savedUser.getId(), 10);

        Set keys = redisTemplate.keys("user::1");
        assertThat(keys.size()).isEqualTo(1);

        ValueOperations valueOperations = redisTemplate.opsForValue();
        UserInfoResponse cacheUserResponse = (UserInfoResponse) valueOperations.get("user::1");

        assertThat(cacheUserResponse.getId()).isEqualTo(savedUser.getId());
        assertThat(cacheUserResponse.getName()).isEqualTo(savedUser.getName());
        assertThat(cacheUserResponse.getAge()).isEqualTo(savedUser.getAge());
    }

    @AfterEach
    void tearDown() {
        databaseCleanup.execute();
        redisTemplate.keys("*").stream()
                .forEach(k -> {
                    redisTemplate.delete(k);
                });
    }

    private void repetitiveCall(Long id, int count) {
        for (int i = 0; i < count; i++) {
            userService.getUser(id);
        }
    }

}