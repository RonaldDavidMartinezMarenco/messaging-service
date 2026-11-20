package com.foodcourt.messaging.infrastructure.config;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import com.foodcourt.messaging.domain.spi.TextMessageSender;
import com.foodcourt.messaging.infrastructure.output.local.LocalMessageSender;
import com.foodcourt.messaging.infrastructure.output.textbee.RejectingMessageSender;
import com.foodcourt.messaging.infrastructure.output.textbee.TextBeeMessageSender;

/** Selects the textbee sender or the explicitly configured local simulation. */
@Configuration
public class MessageSenderConfig {

    private static final Logger log = LoggerFactory.getLogger(MessageSenderConfig.class);
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(2);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(4);

    @Bean
    TextMessageSender textMessageSender(
            TextBeeProperties textBee,
            @Value("${foodcourt.messaging.provider:textbee}") String provider) {
        return select(provider, textBee);
    }

    static TextMessageSender select(String provider, TextBeeProperties textBee) {
        if ("local".equalsIgnoreCase(provider)) {
            log.warn("Order-ready SMS uses the local sender and is not delivered");
            return new LocalMessageSender();
        }
        if (present(provider) && !"textbee".equalsIgnoreCase(provider)) {
            throw new IllegalArgumentException("MESSAGING_PROVIDER must be textbee or local");
        }
        return textBeeSender(textBee);
    }

    private static TextMessageSender textBeeSender(TextBeeProperties textBee) {
        if (!present(textBee.apiKey())) {
            return new RejectingMessageSender("textbee");
        }
        log.info("Order-ready SMS uses textbee");
        return new TextBeeMessageSender(client(textBee.baseUrl()), textBee.apiKey(), textBee.deviceId());
    }

    private static RestClient client(String baseUrl) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(CONNECT_TIMEOUT);
        factory.setReadTimeout(READ_TIMEOUT);
        String root = present(baseUrl) ? baseUrl : "https://api.textbee.dev/api/v1";
        return RestClient.builder().baseUrl(root).requestFactory(factory).build();
    }

    private static boolean present(String value) {
        return value != null && !value.isBlank();
    }
}
