package com.foodcourt.messaging.infrastructure.output.twilio;

/** Creates one Twilio message. Tests replace this so the suite does not call the network. */
public interface TwilioMessageClient {

    void create(String to, String body);
}
