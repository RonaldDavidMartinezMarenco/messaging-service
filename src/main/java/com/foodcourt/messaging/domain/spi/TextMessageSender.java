package com.foodcourt.messaging.domain.spi;

/** Sends one SMS. A failure is {@link com.foodcourt.messaging.domain.exception.NotificationDeliveryFailed}. */
public interface TextMessageSender {

    void send(String phone, String body);
}
