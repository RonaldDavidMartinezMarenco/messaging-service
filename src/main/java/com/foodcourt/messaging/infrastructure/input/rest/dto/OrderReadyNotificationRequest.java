package com.foodcourt.messaging.infrastructure.input.rest.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Body of {@code POST /notifications/order-ready}. The message text is not accepted here.
 * Jackson 3 ignores unknown properties by default, so {@code rejectUnknown} fails the read.
 */
@JsonIgnoreProperties(ignoreUnknown = false)
public record OrderReadyNotificationRequest(
        @NotNull @Positive Long orderId,
        @NotBlank @Size(max = 100) @Pattern(regexp = "^[^\\r\\n]+$") String restaurantName,
        @NotBlank @Size(max = 13) @Pattern(regexp = "^\\+[0-9]+$") String phone,
        @NotBlank @Pattern(regexp = "^[0-9]{6}$") String pin) {

    @JsonAnySetter
    void rejectUnknown(String name, Object ignored) {
        throw new IllegalArgumentException("Unknown field: " + name);
    }
}
