package com.foodcourt.messaging.infrastructure.output.twilio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foodcourt.messaging.domain.spi.TextMessageSender;

/** Accepts the SMS when Twilio credentials are absent. Nothing is delivered and the PIN is not logged. */
public final class LocalMessageSender implements TextMessageSender {

    private static final Logger log = LoggerFactory.getLogger(LocalMessageSender.class);

    @Override
    public void send(String phone, String body) {
        log.warn("Twilio is not configured; an order-ready SMS was accepted locally and not delivered");
    }
}
