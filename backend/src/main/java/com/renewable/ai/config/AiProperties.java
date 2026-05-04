package com.renewable.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ai")
public class AiProperties {
    private boolean enabled = true;
    private String provider = "mock";
    private String apiKey;
    private String model = "gpt-3.5-turbo";
    private String endpoint;
}
