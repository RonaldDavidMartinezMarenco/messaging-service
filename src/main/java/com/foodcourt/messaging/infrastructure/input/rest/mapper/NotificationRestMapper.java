package com.foodcourt.messaging.infrastructure.input.rest.mapper;

import com.foodcourt.messaging.domain.model.OrderReadyNotice;
import com.foodcourt.messaging.infrastructure.input.rest.dto.OrderReadyNotificationRequest;

/** Copies the request into the notice. Applies no rule and does not build the SMS text. */
public final class NotificationRestMapper {

    public OrderReadyNotice toNotice(OrderReadyNotificationRequest request) {
        return new OrderReadyNotice(request.orderId(), request.restaurantName(), request.phone(), request.pin());
    }
}
