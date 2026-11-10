package com.foodcourt.messaging.infrastructure.input.rest;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.foodcourt.messaging.application.handler.NotifyOrderReadyHandler;
import com.foodcourt.messaging.domain.model.DeliveryChannel;
import com.foodcourt.messaging.infrastructure.input.rest.dto.NotificationResponse;
import com.foodcourt.messaging.infrastructure.input.rest.dto.OrderReadyNotificationRequest;
import com.foodcourt.messaging.infrastructure.input.rest.mapper.NotificationRestMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Asks Twilio to send the ready-order PIN. The class is not final: method security needs a CGLIB subclass.
 */
@RestController
@Validated
@Tag(name = "Notifications")
@SecurityRequirement(name = "bearerAuth")
class NotificationController {

    private final NotifyOrderReadyHandler handler;
    private final NotificationRestMapper mapper = new NotificationRestMapper();

    NotificationController(NotifyOrderReadyHandler handler) {
        this.handler = handler;
    }

    @PostMapping("/notifications/order-ready")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(
            summary = "Send the ready-order PIN by SMS",
            description = "The employee token is required. The message text is fixed. "
                    + "phone is E.164 and pin is six digits. The response does not repeat the PIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Twilio accepted the SMS", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = NotificationResponse.class),
                    examples = @ExampleObject(name = "SMS accepted", value = """
                            { "channel": "SMS" }
                            """))),
            @ApiResponse(responseCode = "400", description = "Invalid phone, PIN, or an unknown field"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid access token"),
            @ApiResponse(responseCode = "403", description = "The caller is not an employee"),
            @ApiResponse(responseCode = "502", description = "Twilio rejected the SMS")
    })
    @PreAuthorize("hasRole('EMPLOYEE')")
    public NotificationResponse notify(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = OrderReadyNotificationRequest.class),
                    examples = @ExampleObject(name = "Ronald's order", value = """
                            {
                              "orderId": 1,
                              "restaurantName": "La Parrilla de Carlos",
                              "phone": "+573011590640",
                              "pin": "048215"
                            }
                            """)))
            @Valid @RequestBody OrderReadyNotificationRequest request) {
        DeliveryChannel channel = handler.notify(mapper.toNotice(request));
        return new NotificationResponse(channel);
    }
}
