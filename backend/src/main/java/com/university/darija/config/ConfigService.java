package com.university.darija.config;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

@ApplicationScoped
public class ConfigService {

    private final Config config = ConfigProvider.getConfig();

    public String getGeminiApiKey() {
        return getRequiredValue("GEMINI_API_KEY");
    }

    public String getGeminiModel() {
        return config.getOptionalValue("GEMINI_MODEL", String.class)
                .orElse("gemini-2.5-flash-lite");
    }

    public String getGeminiBaseUrl() {
        return config.getOptionalValue("GEMINI_BASE_URL", String.class)
                .orElse("https://generativelanguage.googleapis.com/v1beta");
    }

    public String getAuthUsername() {
        return config.getOptionalValue("APP_BASIC_AUTH_USERNAME", String.class)
                .orElse("demo");
    }

    public String getAuthPassword() {
        return config.getOptionalValue("APP_BASIC_AUTH_PASSWORD", String.class)
                .orElse("demo123");
    }

    private String getRequiredValue(String key) {
        return config.getOptionalValue(key, String.class)
                .filter(value -> !value.isBlank())
                .orElseThrow(() -> new IllegalStateException(key + " is not configured"));
    }
}
