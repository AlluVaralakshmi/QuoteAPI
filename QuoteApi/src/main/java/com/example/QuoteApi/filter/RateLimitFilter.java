package com.example.QuoteApi.filter;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.QuoteApi.ratelimit.RateLimiterService;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    @Autowired
    private RateLimiterService rateLimiter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String clientIp = request.getRemoteAddr();
        String endpoint = request.getRequestURI();

        RateLimiterService.RateLimitResponse res = rateLimiter.tryConsume(clientIp, endpoint);

        if (!res.allowed) {
            response.setStatus(429);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            String body = String.format(
                    "{\"error\": \"Rate limit exceeded. Try again in %d seconds.\"}",
                    res.retryAfterMs / 1000
            );
            response.getWriter().write(body);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
