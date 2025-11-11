package com.rentacar.web.car.reservation;

import java.time.LocalDate;
import java.util.UUID;

public record CreateReservationRequest(
        UUID reservationId,
        UUID userId,
        UUID carTypeId,
        LocalDate dateFrom,
        LocalDate dateTo
) {
}
