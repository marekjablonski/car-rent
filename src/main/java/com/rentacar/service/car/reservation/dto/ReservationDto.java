package com.rentacar.service.car.reservation.dto;

import com.rentacar.model.ReservationStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ReservationDto(
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
