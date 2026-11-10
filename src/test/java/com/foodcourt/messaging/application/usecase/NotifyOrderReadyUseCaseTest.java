package com.foodcourt.messaging.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.foodcourt.messaging.domain.exception.NotificationDeliveryFailed;
import com.foodcourt.messaging.domain.model.DeliveryChannel;
import com.foodcourt.messaging.domain.model.OrderReadyNotice;
import com.foodcourt.messaging.domain.spi.TextMessageSender;

@ExtendWith(MockitoExtension.class)
class NotifyOrderReadyUseCaseTest {

    private static final OrderReadyNotice NOTICE = new OrderReadyNotice(
            1L, "La Parrilla de Carlos", "+573011590640", "048215");
    private static final String TEXT = """
            Tu pedido #1 de La Parrilla de Carlos está listo.
            Pin de seguridad: 048215""";

    @Mock
    private TextMessageSender messages;

    private NotifyOrderReadyUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new NotifyOrderReadyUseCase(messages);
    }

    @Test
    void notifySendsTheFixedSms() {
        DeliveryChannel channel = useCase.notify(NOTICE);

        assertThat(channel).isEqualTo(DeliveryChannel.SMS);
        verify(messages).send("+573011590640", TEXT);
    }

    @Test
    void notifyPropagatesARejectedSms() {
        doThrow(new NotificationDeliveryFailed()).when(messages).send("+573011590640", TEXT);

        assertThatThrownBy(() -> useCase.notify(NOTICE))
                .isInstanceOf(NotificationDeliveryFailed.class)
                .hasMessage("The order-ready message was rejected");
    }
}
