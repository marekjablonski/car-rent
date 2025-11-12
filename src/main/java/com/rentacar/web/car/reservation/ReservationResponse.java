package com.rentacar.web.car.reservation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rentacar.model.ReservationStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public record ReservationResponse(
        UUID reservationId,
        UUID userId,
        UUID carTypeId,
        LocalDate dateFrom,
        LocalDate dateTo,
        ReservationStatus status,
        Instant lockExpiresAt,
        String paymentId,
        @JsonProperty("_links") Map<String, String> links
) {
}
