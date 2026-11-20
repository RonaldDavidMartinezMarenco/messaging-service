package com.foodcourt.messaging.domain.exception;

/** The SMS provider rejected the request. Mapped to HTTP 502 without exposing the PIN. */
public final class NotificationDeliveryFailed extends RuntimeException {

    public NotificationDeliveryFailed() {
        super("The order-ready message was rejected");
    }
}
