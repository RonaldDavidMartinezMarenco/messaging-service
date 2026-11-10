package com.foodcourt.messaging.infrastructure.input.rest.dto;

import com.foodcourt.messaging.domain.model.DeliveryChannel;

/** Channel that accepted the PIN. The PIN itself is omitted. */
public record NotificationResponse(DeliveryChannel channel) {
}
