package com.foodcourt.messaging.infrastructure.output.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foodcourt.messaging.domain.spi.TextMessageSender;

/** Accepts the notification in local mode without sending an SMS. */
public final class LocalMessageSender implements TextMessageSender {

    private static final Logger log = LoggerFactory.getLogger(LocalMessageSender.class);

    @Override
    public void send(String phone, String body) {
        log.info("Order-ready notification accepted in local mode");
    }
}
