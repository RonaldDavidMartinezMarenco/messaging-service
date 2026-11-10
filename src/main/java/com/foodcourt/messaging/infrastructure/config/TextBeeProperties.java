package com.foodcourt.messaging.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Credentials for the textbee gateway. {@code deviceId} is optional: an empty value uses the default phone.
 */
@ConfigurationProperties(prefix = "textbee")
public record TextBeeProperties(String apiKey, String baseUrl, String deviceId) {
}
