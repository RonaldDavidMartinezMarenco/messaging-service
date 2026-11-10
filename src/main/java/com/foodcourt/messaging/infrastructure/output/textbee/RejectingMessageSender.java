package com.foodcourt.messaging.infrastructure.output.textbee;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foodcourt.messaging.domain.exception.NotificationDeliveryFailed;
import com.foodcourt.messaging.domain.spi.TextMessageSender;

/** Refuses the send when the selected provider has no credentials. The PIN is not logged. */
public final class RejectingMessageSender implements TextMessageSender {

    private static final Logger log = LoggerFactory.getLogger(RejectingMessageSender.class);

    private final String provider;

    public RejectingMessageSender(String provider) {
        this.provider = provider;
    }

    @Override
    public void send(String phone, String body) {
        log.warn("{} is not configured; the order-ready SMS was not sent", provider);
        throw new NotificationDeliveryFailed();
    }
}
