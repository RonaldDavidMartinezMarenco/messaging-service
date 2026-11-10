package com.foodcourt.messaging.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.foodcourt.messaging.infrastructure.output.textbee.RejectingMessageSender;
import com.foodcourt.messaging.infrastructure.output.textbee.TextBeeMessageSender;
import com.foodcourt.messaging.infrastructure.output.twilio.LocalMessageSender;
import com.foodcourt.messaging.infrastructure.output.twilio.TwilioMessageSender;

class MessageSenderConfigTest {

    @Test
    void selectUsesTextBeeWhenTheKeyIsPresent() {
        assertThat(MessageSenderConfig.select("textbee", textBee("key"), twilio("", "", "")))
                .isInstanceOf(TextBeeMessageSender.class);
    }

    @Test
    void selectRefusesTextBeeWhenTheKeyIsMissing() {
        assertThat(MessageSenderConfig.select("textbee", textBee(" "), twilio("AC", "token", "+17372508034")))
                .isInstanceOf(RejectingMessageSender.class);
    }

    @Test
    void selectUsesTwilioOnlyWhenAsked() {
        assertThat(MessageSenderConfig.select("twilio", textBee("key"), twilio("AC", "token", "+17372508034")))
                .isInstanceOf(TwilioMessageSender.class);
    }

    @Test
    void selectUsesTheLocalSenderOnlyWhenAsked() {
        assertThat(MessageSenderConfig.select("local", textBee("key"), twilio("AC", "token", "+17372508034")))
                .isInstanceOf(LocalMessageSender.class);
    }

    private static TextBeeProperties textBee(String apiKey) {
        return new TextBeeProperties(apiKey, "https://api.textbee.dev/api/v1", "");
    }

    private static TwilioProperties twilio(String sid, String token, String from) {
        return new TwilioProperties(sid, token, from);
    }
}
