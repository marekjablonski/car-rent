package com.rentacar.service.car.reservation;

import com.rentacar.service.car.reservation.dto.ReservationDto;

import java.util.UUID;

public interface ReservationService {

    ReservationDto createReservation(CreateReservationCommand command);

    ReservationDto confirmPayment(UUID reservationId, String paymentId);

    ReservationDto getReservation(UUID reservationId);
}
