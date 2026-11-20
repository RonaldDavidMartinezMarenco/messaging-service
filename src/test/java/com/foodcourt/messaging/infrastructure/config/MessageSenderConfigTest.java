package com.foodcourt.messaging.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;

import com.foodcourt.messaging.infrastructure.output.local.LocalMessageSender;
import com.foodcourt.messaging.infrastructure.output.textbee.RejectingMessageSender;
import com.foodcourt.messaging.infrastructure.output.textbee.TextBeeMessageSender;

class MessageSenderConfigTest {

    @Test
    void selectUsesTextBeeWhenTheKeyIsPresent() {
        assertThat(MessageSenderConfig.select("textbee", textBee("key")))
                .isInstanceOf(TextBeeMessageSender.class);
    }

    @Test
    void selectRefusesTextBeeWhenTheKeyIsMissing() {
        assertThat(MessageSenderConfig.select("textbee", textBee(" ")))
                .isInstanceOf(RejectingMessageSender.class);
    }

    @Test
    void selectRejectsUnsupportedProvidersEvenWhenTextBeeIsConfigured() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> MessageSenderConfig.select("unsupported", textBee("key")))
                .withMessage("MESSAGING_PROVIDER must be textbee or local");
    }

    @Test
    void selectUsesTheLocalSenderOnlyWhenAsked() {
        assertThat(MessageSenderConfig.select("local", textBee("key")))
                .isInstanceOf(LocalMessageSender.class);
    }

    private static TextBeeProperties textBee(String apiKey) {
        return new TextBeeProperties(apiKey, "https://api.textbee.dev/api/v1", "");
    }
}
