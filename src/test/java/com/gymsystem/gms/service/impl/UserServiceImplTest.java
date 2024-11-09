package com.gymsystem.gms.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@TestPropertySource(properties = {
        "jwt.secret=test_jwt_secret",
        "api.stripe.key=test_stripe_key"
})
class UserServiceImplTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void init() {
    }

    @Test
    void loadUserByUsername() {
    }

    @Test
    void register() {
    }

    @Test
    void addNewUser() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void resetPassword() {
    }

    @Test
    void setNewPassword() {
    }

    @Test
    void updateProfileImage() {
    }

    @Test
    void findUserByCustomerId() {
    }

    @Test
    void getUsers() {
    }

    @Test
    void findUserByUsername() {
    }

    @Test
    void findUserByEmail() {
    }

    @Test
    void findUserByUsernameOrEmail() {
    }

    @Test
    void findUserById() {
    }
}