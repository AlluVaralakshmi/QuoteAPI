package com.example.QuoteApi.ratelimit;



import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RateLimiterServiceTest {

    private RateLimiterService rateLimiter;

    @BeforeEach
    void setUp() {
        rateLimiter = new RateLimiterService();

        // set custom test limits (simulate @Value injection)
        rateLimiter = new RateLimiterService() {{
            quoteLimit = 5;       // 5 requests per minute
            quoteWindowMs = 60000;
            testLimit = 3;        // 3 requests per 30s
            testWindowMs = 30000;
        }};
    }

    @Test
    void testWithinLimit() {
        String ip = "127.0.0.1";

        for (int i = 0; i < 5; i++) {
            RateLimiterService.RateLimitResponse res = rateLimiter.tryConsume(ip, "/api/quote");
            assertTrue(res.allowed, "Request " + (i+1) + " should be allowed");
        }
    }

    @Test
    void testExceedLimit() {
        String ip = "127.0.0.1";

        // 5 allowed
        for (int i = 0; i < 5; i++) {
            rateLimiter.tryConsume(ip, "/api/quote");
        }

        // 6th should fail
        RateLimiterService.RateLimitResponse res = rateLimiter.tryConsume(ip, "/api/quote");
        assertFalse(res.allowed, "6th request should be blocked");
        assertTrue(res.retryAfterMs > 0, "Retry-After should be greater than 0");
    }

    @Test
    void testSeparateEndpointsHaveSeparateLimits() {
        String ip = "127.0.0.1";

        // Consume 3 requests for /api/test
        for (int i = 0; i < 3; i++) {
            assertTrue(rateLimiter.tryConsume(ip, "/api/test").allowed);
        }

        // 4th should fail
        assertFalse(rateLimiter.tryConsume(ip, "/api/test").allowed);

        // But /api/quote should still work (5 requests allowed)
        for (int i = 0; i < 5; i++) {
            assertTrue(rateLimiter.tryConsume(ip, "/api/quote").allowed);
        }
    }
}

