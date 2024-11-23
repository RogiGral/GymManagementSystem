package com.gymsystem.gms;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "jwt.secret=test_jwt_secret",
        "api.stripe.key=test_stripe_key"
})
class GmsApplicationTests {

    @Test
    void contextLoads() {
    }

}
