package com.example.QuoteApi.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.QuoteApi.service.QuoteService;


@RestController
@RequestMapping("/api")
public class QuoteController {

    private final QuoteService quoteService;

    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    // Test endpoint for custom limit
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> testEndpoint() {
        String q = quoteService.randomQuote();
        return ResponseEntity.ok(Map.of("quote", q));
    }

    // Quote endpoint
    @GetMapping("/quote")
    public ResponseEntity<Map<String, String>> getQuote() {
        String q = quoteService.randomQuote();
        return ResponseEntity.ok(Map.of("quote", q));
    }
}
