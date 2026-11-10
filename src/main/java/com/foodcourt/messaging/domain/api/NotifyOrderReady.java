package com.foodcourt.messaging.domain.api;

import com.foodcourt.messaging.domain.model.DeliveryChannel;
import com.foodcourt.messaging.domain.model.OrderReadyNotice;

/** Inbound port that sends the ready-order PIN. */
public interface NotifyOrderReady {

    DeliveryChannel notify(OrderReadyNotice notice);
}
