package com.foodcourt.messaging.domain.exception;

/** Twilio did not accept the SMS. Mapped to HTTP 502. The message does not include the PIN. */
public final class NotificationDeliveryFailed extends RuntimeException {

    public NotificationDeliveryFailed() {
        super("The order-ready message was rejected");
    }
}
