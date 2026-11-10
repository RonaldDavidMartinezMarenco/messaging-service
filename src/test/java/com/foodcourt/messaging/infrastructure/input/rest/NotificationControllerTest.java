package com.foodcourt.messaging.infrastructure.input.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.foodcourt.messaging.application.handler.NotifyOrderReadyHandler;
import com.foodcourt.messaging.domain.exception.NotificationDeliveryFailed;
import com.foodcourt.messaging.domain.model.DeliveryChannel;
import com.foodcourt.messaging.domain.model.OrderReadyNotice;
import com.foodcourt.messaging.infrastructure.exception.ApiExceptionHandler;
import com.foodcourt.messaging.infrastructure.security.SecurityConfig;
import com.foodcourt.messaging.infrastructure.security.SecurityProblemHandlers;

@WebMvcTest(NotificationController.class)
@Import({ApiExceptionHandler.class, SecurityConfig.class, SecurityProblemHandlers.class})
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotifyOrderReadyHandler handler;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void notifyAcceptsTheSmsWithoutReturningThePin() throws Exception {
        when(handler.notify(new OrderReadyNotice(1L, "La Parrilla de Carlos", "+573011590640", "048215")))
                .thenReturn(DeliveryChannel.SMS);

        mockMvc.perform(post("/notifications/order-ready")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EMPLOYEE")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body()))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.channel").value("SMS"))
                .andExpect(jsonPath("$.pin").doesNotExist());
    }

    @Test
    void notifyRejectsAPhoneThatIsNotE164() throws Exception {
        mockMvc.perform(post("/notifications/order-ready")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EMPLOYEE")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body().replace("+573011590640", "3011590640")))
                .andExpect(status().isBadRequest());
        verify(handler, never()).notify(any());
    }

    @Test
    void notifyRejectsAPinThatIsNotSixDigits() throws Exception {
        mockMvc.perform(post("/notifications/order-ready")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EMPLOYEE")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body().replace("048215", "48215")))
                .andExpect(status().isBadRequest());
        verify(handler, never()).notify(any());
    }

    @Test
    void notifyRejectsAnUnknownField() throws Exception {
        mockMvc.perform(post("/notifications/order-ready")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EMPLOYEE")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"orderId":1,"restaurantName":"La Parrilla de Carlos","phone":"+573011590640","pin":"048215","text":"hello"}
                                """))
                .andExpect(status().isBadRequest());
        verify(handler, never()).notify(any());
    }

    @Test
    void notifyReportsARejectedSms() throws Exception {
        when(handler.notify(any(OrderReadyNotice.class))).thenThrow(new NotificationDeliveryFailed());

        mockMvc.perform(post("/notifications/order-ready")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_EMPLOYEE")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body()))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.detail").value("The order-ready message was rejected"))
                .andExpect(jsonPath("$.detail").value(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("048215"))));
    }

    @Test
    void notifyRejectsAMissingToken() throws Exception {
        mockMvc.perform(post("/notifications/order-ready")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body()))
                .andExpect(status().isUnauthorized());
        verify(handler, never()).notify(any());
    }

    @Test
    void notifyRejectsACustomer() throws Exception {
        mockMvc.perform(post("/notifications/order-ready")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CUSTOMER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body()))
                .andExpect(status().isForbidden());
        verify(handler, never()).notify(any());
    }

    private static String body() {
        return """
                {"orderId":1,"restaurantName":"La Parrilla de Carlos","phone":"+573011590640","pin":"048215"}
                """;
    }
}
