package com.foodcourt.messaging.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "twilio")
public record TwilioProperties(String accountSid, String authToken, String fromNumber) {
}
