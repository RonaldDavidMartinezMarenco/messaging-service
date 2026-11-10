package com.foodcourt.messaging.application.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.foodcourt.messaging.domain.api.NotifyOrderReady;
import com.foodcourt.messaging.domain.model.DeliveryChannel;
import com.foodcourt.messaging.domain.model.OrderReadyNotice;

@ExtendWith(MockitoExtension.class)
class NotifyOrderReadyHandlerTest {

    @Mock
    private NotifyOrderReady notifyOrderReady;

    @Test
    void notifyForwardsTheNotice() {
        OrderReadyNotice notice = new OrderReadyNotice(1L, "La Parrilla de Carlos", "+573011590640", "048215");
        when(notifyOrderReady.notify(notice)).thenReturn(DeliveryChannel.SMS);
        NotifyOrderReadyHandler handler = new NotifyOrderReadyHandler(notifyOrderReady);

        assertThat(handler.notify(notice)).isEqualTo(DeliveryChannel.SMS);
        verify(notifyOrderReady).notify(notice);
    }
}
