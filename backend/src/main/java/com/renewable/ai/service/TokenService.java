package com.renewable.ai.service;

import com.renewable.ai.config.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class TokenService {

    @Autowired
    private JwtProperties jwtProperties;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        String secret = jwtProperties.getSecret();
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT secret is not configured. Set jwt.secret property or JWT_SECRET env var.");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(Long userId, String role, String username) {
        Instant now = Instant.now();
        Instant expiry = now.plus(getTtlMinutes(jwtProperties.getAccessTokenTtl()), ChronoUnit.MINUTES);
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .claim("username", username)
                .issuer(jwtProperties.getIssuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

    public Claims parseAccessToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            throw e;
        }
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = parseAccessToken(token);
        return Long.parseLong(claims.getSubject());
    }

    public String getRoleFromToken(String token) {
        Claims claims = parseAccessToken(token);
        return claims.get("role", String.class);
    }

    private int getTtlMinutes(String ttl) {
        if (ttl == null) return 15;
        ttl = ttl.trim().toLowerCase();
        if (ttl.endsWith("m")) {
            return Integer.parseInt(ttl.substring(0, ttl.length() - 1));
        }
        if (ttl.endsWith("h")) {
            return Integer.parseInt(ttl.substring(0, ttl.length() - 1)) * 60;
        }
        if (ttl.endsWith("d")) {
            return Integer.parseInt(ttl.substring(0, ttl.length() - 1)) * 60 * 24;
        }
        return Integer.parseInt(ttl);
    }

    public int getRefreshTokenTtlDays() {
        String ttl = jwtProperties.getRefreshTokenTtl();
        if (ttl == null) return 7;
        ttl = ttl.trim().toLowerCase();
        if (ttl.endsWith("d")) {
            return Integer.parseInt(ttl.substring(0, ttl.length() - 1));
        }
        return 7;
    }

    private static final ConcurrentHashMap<String, RefreshTokenEntry> REFRESH_TOKEN_MAP = new ConcurrentHashMap<>();

    private static class RefreshTokenEntry {
        final Long userId;
        final Instant expiresAt;
        RefreshTokenEntry(Long userId, Instant expiresAt) {
            this.userId = userId;
            this.expiresAt = expiresAt;
        }
    }

    public void storeRefreshToken(String refreshToken, Long userId) {
        Instant expiresAt = Instant.now().plus(getRefreshTokenTtlDays(), ChronoUnit.DAYS);
        REFRESH_TOKEN_MAP.put(refreshToken, new RefreshTokenEntry(userId, expiresAt));
    }

    public Long validateRefreshToken(String refreshToken) {
        RefreshTokenEntry entry = REFRESH_TOKEN_MAP.get(refreshToken);
        if (entry == null) {
            return null;
        }
        if (Instant.now().isAfter(entry.expiresAt)) {
            REFRESH_TOKEN_MAP.remove(refreshToken);
            return null;
        }
        return entry.userId;
    }

    public void revokeRefreshToken(String refreshToken) {
        REFRESH_TOKEN_MAP.remove(refreshToken);
    }

    public void revokeAllUserRefreshTokens(Long userId) {
        REFRESH_TOKEN_MAP.entrySet().removeIf(e -> e.getValue().userId.equals(userId));
    }
}
