package com.example.QuoteApi.ratelimit;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {

    @Value("${ratelimit.quote.requests:5}")
    private int quoteLimit;

    @Value("${ratelimit.quote.windowMs:60000}")
    private long quoteWindowMs;

    @Value("${ratelimit.test.requests:3}")
    private int testLimit;

    @Value("${ratelimit.test.windowMs:30000}")
    private long testWindowMs;

    private final ConcurrentHashMap<String, RequestCounter> map = new ConcurrentHashMap<>();

    public RateLimitResponse tryConsume(String ip, String endpoint) {
        int limit;
        long windowMs;

        switch (endpoint) {
            case "/api/quote":
                limit = quoteLimit;
                windowMs = quoteWindowMs;
                break;
            case "/api/test":
                limit = testLimit;
                windowMs = testWindowMs;
                break;
            default:
                limit = 5;
                windowMs = 60000;
        }

        long now = Instant.now().toEpochMilli();
        RequestCounter counter = map.computeIfAbsent(ip + endpoint, k -> new RequestCounter(now, new AtomicInteger(0)));

        synchronized (counter) {
            if (now - counter.windowStart >= windowMs) {
                counter.windowStart = now;
                counter.count.set(0);
            }

            int current = counter.count.incrementAndGet();
            if (current <= limit) {
                long resetIn = windowMs - (now - counter.windowStart);
                return new RateLimitResponse(true, 0, resetIn);
            } else {
                long resetIn = windowMs - (now - counter.windowStart);
                return new RateLimitResponse(false, current - limit, resetIn);
            }
        }
    }

    private static class RequestCounter {
        volatile long windowStart;
        AtomicInteger count;

        RequestCounter(long windowStart, AtomicInteger count) {
            this.windowStart = windowStart;
            this.count = count;
        }
    }

    public static class RateLimitResponse {
        public final boolean allowed;
        public final int overBy;
        public final long retryAfterMs;

        public RateLimitResponse(boolean allowed, int overBy, long retryAfterMs) {
            this.allowed = allowed;
            this.overBy = overBy;
            this.retryAfterMs = retryAfterMs;
        }
    }
}
