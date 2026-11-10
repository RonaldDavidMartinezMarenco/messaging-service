package com.foodcourt.messaging.application.handler;

import org.springframework.stereotype.Service;

import com.foodcourt.messaging.domain.api.NotifyOrderReady;
import com.foodcourt.messaging.domain.model.DeliveryChannel;
import com.foodcourt.messaging.domain.model.OrderReadyNotice;

/** Entry the controller calls. The text and the provider stay in the use case. */
@Service
public final class NotifyOrderReadyHandler {

    private final NotifyOrderReady notifyOrderReady;

    public NotifyOrderReadyHandler(NotifyOrderReady notifyOrderReady) {
        this.notifyOrderReady = notifyOrderReady;
    }

    public DeliveryChannel notify(OrderReadyNotice notice) {
        return notifyOrderReady.notify(notice);
    }
}
