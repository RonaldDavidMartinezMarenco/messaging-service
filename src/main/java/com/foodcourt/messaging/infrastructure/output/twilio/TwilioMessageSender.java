package com.foodcourt.messaging.infrastructure.output.twilio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foodcourt.messaging.domain.exception.NotificationDeliveryFailed;
import com.foodcourt.messaging.domain.spi.TextMessageSender;
import com.twilio.exception.ApiException;
import com.twilio.exception.TwilioException;

/** Sends through {@link TwilioMessageClient}. The failure carries no PIN and no message body. */
public final class TwilioMessageSender implements TextMessageSender {

    private static final Logger log = LoggerFactory.getLogger(TwilioMessageSender.class);

    private final TwilioMessageClient messages;

    public TwilioMessageSender(TwilioMessageClient messages) {
        this.messages = messages;
    }

    @Override
    public void send(String phone, String body) {
        try {
            messages.create(phone, body);
        } catch (ApiException failure) {
            log.warn("Twilio did not accept an order-ready SMS, status {} code {}", failure.getStatusCode(), failure.getCode());
            throw new NotificationDeliveryFailed();
        } catch (TwilioException failure) {
            log.warn("Twilio did not accept an order-ready SMS ({})", failure.getClass().getSimpleName());
            throw new NotificationDeliveryFailed();
        }
    }
}
