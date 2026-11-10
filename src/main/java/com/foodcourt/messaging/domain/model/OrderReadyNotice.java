package com.foodcourt.messaging.domain.model;

/** Facts the SMS needs. The text is built by the use case, not by the caller. */
public record OrderReadyNotice(long orderId, String restaurantName, String phone, String pin) {
}
