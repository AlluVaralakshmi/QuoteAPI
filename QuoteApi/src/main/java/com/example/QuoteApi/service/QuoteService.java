package com.example.QuoteApi.service;

import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class QuoteService {

    private final List<String> quotes = List.of(
        "The only way to do great work is to love what you do. - Steve Jobs",
        "Success is not final, failure is not fatal: it is the courage to continue that counts. - Winston Churchill",
        "Believe you can and you're halfway there. - Theodore Roosevelt",
        "Do what you can, with what you have, where you are. - Theodore Roosevelt",
        "The harder I work, the luckier I get. - Gary Player"
    );

    private final Random random = new Random();

    public String randomQuote() {
        return quotes.get(random.nextInt(quotes.size()));
    }
}
