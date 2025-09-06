package com.example.QuoteApi.ratelimit;



import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RateLimiterServiceTest {

    private RateLimiterService rateLimiter;

    @BeforeEach
    void setUp() {
        rateLimiter = new RateLimiterService();

       
        rateLimiter = new RateLimiterService() {{
            quoteLimit = 5;      
            quoteWindowMs = 60000;
            testLimit = 3;        
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

       
        for (int i = 0; i < 5; i++) {
            rateLimiter.tryConsume(ip, "/api/quote");
        }

      
        RateLimiterService.RateLimitResponse res = rateLimiter.tryConsume(ip, "/api/quote");
        assertFalse(res.allowed, "6th request should be blocked");
        assertTrue(res.retryAfterMs > 0, "Retry-After should be greater than 0");
    }

    @Test
    void testSeparateEndpointsHaveSeparateLimits() {
        String ip = "127.0.0.1";

      
        for (int i = 0; i < 3; i++) {
            assertTrue(rateLimiter.tryConsume(ip, "/api/test").allowed);
        }

      
        assertFalse(rateLimiter.tryConsume(ip, "/api/test").allowed);

       
        for (int i = 0; i < 5; i++) {
            assertTrue(rateLimiter.tryConsume(ip, "/api/quote").allowed);
        }
    }
}

