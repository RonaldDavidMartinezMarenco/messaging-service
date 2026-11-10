package com.foodcourt.messaging.infrastructure.output.twilio;

import org.apache.http.client.config.RequestConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foodcourt.messaging.infrastructure.config.TwilioProperties;
import com.twilio.http.NetworkHttpClient;
import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

/** Calls the Messages resource with the account credentials. Timeouts stay under the plaza read timeout. */
public final class SdkTwilioMessageClient implements TwilioMessageClient {

    private static final int TIMEOUT_MILLIS = 3000;

    private static final Logger log = LoggerFactory.getLogger(SdkTwilioMessageClient.class);

    private final TwilioRestClient client;
    private final String from;

    public SdkTwilioMessageClient(TwilioProperties properties) {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(TIMEOUT_MILLIS)
                .setSocketTimeout(TIMEOUT_MILLIS)
                .build();
        this.client = new TwilioRestClient.Builder(properties.accountSid(), properties.authToken())
                .httpClient(new NetworkHttpClient(config))
                .build();
        this.from = properties.fromNumber();
        log.info("Twilio SMS sender is configured");
    }

    @Override
    public void create(String to, String body) {
        Message.creator(new PhoneNumber(to), new PhoneNumber(from), body).create(client);
    }
}
