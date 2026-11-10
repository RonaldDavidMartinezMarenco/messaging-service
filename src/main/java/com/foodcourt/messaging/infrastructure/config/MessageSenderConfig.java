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
import com.foodcourt.messaging.infrastructure.output.textbee.RejectingMessageSender;
import com.foodcourt.messaging.infrastructure.output.textbee.TextBeeMessageSender;
import com.foodcourt.messaging.infrastructure.output.twilio.LocalMessageSender;
import com.foodcourt.messaging.infrastructure.output.twilio.SdkTwilioMessageClient;
import com.foodcourt.messaging.infrastructure.output.twilio.TwilioMessageSender;

/**
 * textbee is the sender used unless {@code foodcourt.messaging.provider} says {@code twilio} or {@code local}.
 * A missing key refuses the send. It does not fall through to the other provider.
 */
@Configuration
public class MessageSenderConfig {

    private static final Logger log = LoggerFactory.getLogger(MessageSenderConfig.class);
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(2);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(4);

    @Bean
    TextMessageSender textMessageSender(
            TextBeeProperties textBee,
            TwilioProperties twilio,
            @Value("${foodcourt.messaging.provider:textbee}") String provider) {
        return select(provider, textBee, twilio);
    }

    static TextMessageSender select(String provider, TextBeeProperties textBee, TwilioProperties twilio) {
        if (isTwilio(provider)) {
            return twilioSender(twilio);
        }
        if (isLocal(provider)) {
            log.warn("Order-ready SMS uses the local sender and is not delivered");
            return new LocalMessageSender();
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

    private static TextMessageSender twilioSender(TwilioProperties twilio) {
        if (!present(twilio.accountSid()) || !present(twilio.authToken()) || !present(twilio.fromNumber())) {
            return new RejectingMessageSender("Twilio");
        }
        log.info("Order-ready SMS uses Twilio");
        return new TwilioMessageSender(new SdkTwilioMessageClient(twilio));
    }

    private static RestClient client(String baseUrl) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(CONNECT_TIMEOUT);
        factory.setReadTimeout(READ_TIMEOUT);
        String root = present(baseUrl) ? baseUrl : "https://api.textbee.dev/api/v1";
        return RestClient.builder().baseUrl(root).requestFactory(factory).build();
    }

    private static boolean isTwilio(String provider) {
        return "twilio".equalsIgnoreCase(provider);
    }

    private static boolean isLocal(String provider) {
        return "local".equalsIgnoreCase(provider);
    }

    private static boolean present(String value) {
        return value != null && !value.isBlank();
    }
}
