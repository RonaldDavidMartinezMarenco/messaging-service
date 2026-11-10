package com.foodcourt.messaging.application.usecase;

import org.springframework.stereotype.Service;

import com.foodcourt.messaging.domain.api.NotifyOrderReady;
import com.foodcourt.messaging.domain.model.DeliveryChannel;
import com.foodcourt.messaging.domain.model.OrderReadyNotice;
import com.foodcourt.messaging.domain.spi.TextMessageSender;

/**
 * Sends the ready-order PIN by SMS. The body is fixed here so the HTTP client cannot choose the text.
 */
@Service
public final class NotifyOrderReadyUseCase implements NotifyOrderReady {

    private final TextMessageSender messages;

    public NotifyOrderReadyUseCase(TextMessageSender messages) {
        this.messages = messages;
    }

    @Override
    public DeliveryChannel notify(OrderReadyNotice notice) {
        messages.send(notice.phone(), text(notice));
        return DeliveryChannel.SMS;
    }

    private static String text(OrderReadyNotice notice) {
        return "Tu pedido #" + notice.orderId() + " de " + notice.restaurantName() + " está listo.\n"
                + "Pin de seguridad: " + notice.pin();
    }
}
