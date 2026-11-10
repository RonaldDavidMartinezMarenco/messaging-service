package com.foodcourt.messaging.infrastructure.output.textbee;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.foodcourt.messaging.domain.exception.NotificationDeliveryFailed;
import com.foodcourt.messaging.domain.spi.TextMessageSender;

/**
 * Sends one SMS through textbee. HTTP 200 means the gateway accepted the text.
 * The phone delivers it afterwards. The failure carries no PIN and no message body.
 */
public final class TextBeeMessageSender implements TextMessageSender {

    private static final Logger log = LoggerFactory.getLogger(TextBeeMessageSender.class);

    private final RestClient client;
    private final String apiKey;
    private final String deviceId;

    public TextBeeMessageSender(RestClient client, String apiKey, String deviceId) {
        this.client = client;
        this.apiKey = apiKey;
        this.deviceId = blankToNull(deviceId);
    }

    @Override
    public void send(String phone, String body) {
        try {
            client.post()
                    .uri("/gateway/send-sms")
                    .header("x-api-key", apiKey)
                    .body(payload(phone, body))
                    .exchange(this::requireAccepted);
        } catch (RestClientException failure) {
            log.warn("textbee did not accept an order-ready SMS");
            throw new NotificationDeliveryFailed();
        }
    }

    private Void requireAccepted(
            org.springframework.http.HttpRequest request,
            RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse response) throws IOException {
        HttpStatusCode status = response.getStatusCode();
        if (status.is2xxSuccessful()) {
            return null;
        }
        log.warn("textbee did not accept an order-ready SMS, status {}", status.value());
        throw new NotificationDeliveryFailed();
    }

    private Map<String, Object> payload(String phone, String body) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("recipients", List.of(phone));
        payload.put("message", body);
        if (deviceId != null) {
            payload.put("deviceId", deviceId);
        }
        return payload;
    }

    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value;
    }
}
