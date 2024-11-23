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
class UserWorkoutServiceImplTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllUserWorkouts() {
    }

    @Test
    void addUserToWorkout() {
    }

    @Test
    void deleteUserWorkout() {
    }

    @Test
    void testDeleteUserWorkout() {
    }

    @Test
    void listAllUsersJoined() {
    }
}