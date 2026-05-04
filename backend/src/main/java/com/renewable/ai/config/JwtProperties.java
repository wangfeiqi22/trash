package com.renewable.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret;
    private String accessTokenTtl = "15m";
    private String refreshTokenTtl = "7d";
    private String issuer = "smart-waste-system";
}
