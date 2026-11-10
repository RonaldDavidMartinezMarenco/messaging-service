package com.foodcourt.messaging.infrastructure.output.textbee;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.foodcourt.messaging.domain.exception.NotificationDeliveryFailed;

class TextBeeMessageSenderTest {

    private MockRestServiceServer server;
    private RestClient.Builder builder;

    @BeforeEach
    void setUp() {
        builder = RestClient.builder().baseUrl("https://api.textbee.dev/api/v1");
        server = MockRestServiceServer.bindTo(builder).build();
    }

    @Test
    void sendPostsThePhoneAndTheMessage() {
        server.expect(requestTo("https://api.textbee.dev/api/v1/gateway/send-sms"))
                .andExpect(header("x-api-key", "key-1"))
                .andRespond(withSuccess("{\"smsBatchId\":\"batch-1\"}", MediaType.APPLICATION_JSON));
        TextBeeMessageSender sender = new TextBeeMessageSender(builder.build(), "key-1", " ");

        assertThatCode(() -> sender.send("+573011590640", "Tu pedido 1 está listo. Pin de seguridad: 048215"))
                .doesNotThrowAnyException();
        server.verify();
    }

    @Test
    void sendRejectsAGatewayError() {
        server.expect(requestTo("https://api.textbee.dev/api/v1/gateway/send-sms"))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));
        TextBeeMessageSender sender = new TextBeeMessageSender(builder.build(), "key-1", null);

        assertThatThrownBy(() -> sender.send("+573011590640", "Tu pedido 1 está listo. Pin de seguridad: 048215"))
                .isInstanceOf(NotificationDeliveryFailed.class)
                .hasMessage("The order-ready message was rejected");
    }
}
