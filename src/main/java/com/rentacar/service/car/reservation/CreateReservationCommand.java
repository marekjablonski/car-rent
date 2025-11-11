package com.rentacar.service.car.reservation;

import java.time.LocalDate;
import java.util.UUID;

public record CreateReservationCommand(
        UUID reservationId,
        UUID userId,
        UUID carTypeId,
        LocalDate dateFrom,
        LocalDate dateTo
) {
}
