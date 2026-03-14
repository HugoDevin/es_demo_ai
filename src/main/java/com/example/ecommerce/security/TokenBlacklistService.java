package com.example.ecommerce.security;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {
    private final Set<String> tokens = ConcurrentHashMap.newKeySet();
    public void blacklist(String token) { tokens.add(token); }
    public boolean isBlacklisted(String token) { return tokens.contains(token); }
}
