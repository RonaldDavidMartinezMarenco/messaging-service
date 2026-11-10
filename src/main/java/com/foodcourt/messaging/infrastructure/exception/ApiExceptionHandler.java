package com.foodcourt.messaging.infrastructure.exception;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.foodcourt.messaging.domain.exception.NotificationDeliveryFailed;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

/**
 * Maps delivery failures to {@code application/problem+json}.
 * Bean Validation is left to Spring, which already returns HTTP 400.
 * The detail never includes the PIN.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(NotificationDeliveryFailed.class)
    ProblemDetail rejected(NotificationDeliveryFailed exception, HttpServletRequest request) {
        return problem(HttpStatus.BAD_GATEWAY, "Message rejected", exception, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ProblemDetail invalidConstraint(ConstraintViolationException exception, HttpServletRequest request) {
        return problem(HttpStatus.BAD_REQUEST, "Invalid request", exception, request);
    }

    private static ProblemDetail problem(
            HttpStatus status,
            String title,
            RuntimeException exception,
            HttpServletRequest request) {
        ProblemDetail body = ProblemDetail.forStatusAndDetail(status, exception.getMessage());
        body.setTitle(title);
        body.setInstance(URI.create(request.getRequestURI()));
        return body;
    }
}
