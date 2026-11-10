package com.foodcourt.messaging.infrastructure.output.twilio;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.foodcourt.messaging.domain.exception.NotificationDeliveryFailed;
import com.twilio.exception.ApiException;

@ExtendWith(MockitoExtension.class)
class TwilioMessageSenderTest {

    @Mock
    private TwilioMessageClient messages;

    @Test
    void sendKeepsThePhoneAndTheBody() {
        TwilioMessageSender sender = new TwilioMessageSender(messages);

        sender.send("+573011590640", "Tu pedido 1 está listo. Pin de seguridad: 048215");

        verify(messages).create("+573011590640", "Tu pedido 1 está listo. Pin de seguridad: 048215");
    }

    @Test
    void sendHidesATwilioRejection() {
        TwilioMessageSender sender = new TwilioMessageSender(messages);
        doThrow(new ApiException("provider rejected the body")).when(messages).create("+17372508034", "secret");

        assertThatThrownBy(() -> sender.send("+17372508034", "secret"))
                .isInstanceOf(NotificationDeliveryFailed.class)
                .hasMessage("The order-ready message was rejected");
    }
}
