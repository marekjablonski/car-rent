package com.rentacar.web.car.reservation;

import com.rentacar.model.reservation.ReservationStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ReservationResponse(
        UUID reservationId,
        UUID userId,
        UUID carTypeId,
        LocalDate dateFrom,
        LocalDate dateTo,
        ReservationStatus status,
        Instant lockExpiresAt,
        String paymentId
) {
}
