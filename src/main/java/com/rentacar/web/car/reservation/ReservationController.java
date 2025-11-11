package com.rentacar.web.car.reservation;

import com.rentacar.model.reservation.Reservation;
import com.rentacar.service.car.reservation.CreateReservationCommand;
import com.rentacar.service.car.reservation.ReservationService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/reservation")
    public ReservationResponse createReservation(@RequestBody CreateReservationRequest request) {
        UUID reservationId = request.reservationId() != null ? request.reservationId() : UUID.randomUUID();
        CreateReservationCommand command = new CreateReservationCommand(
                reservationId,
                request.userId(),
                request.carTypeId(),
                request.dateFrom(),
                request.dateTo()
        );
        Reservation reservation = reservationService.createReservation(command);
        return toResponse(reservation);
    }

    @PostMapping("/payment")
    public ReservationResponse confirmPayment(@RequestBody PaymentRequest request) {
        if (request.reservationId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "reservationId is required");
        }
        if (request.paymentId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "paymentId is required");
        }
        Reservation reservation = reservationService.confirmPayment(request.reservationId(), request.paymentId().toString());
        return toResponse(reservation);
    }

    private static ReservationResponse toResponse(Reservation reservation) {
        return new ReservationResponse(
                reservation.id(),
                reservation.userId(),
                reservation.carTypeId(),
                reservation.dateRange().start(),
                reservation.dateRange().end(),
                reservation.status(),
                reservation.lockExpiresAt(),
                reservation.paymentId()
        );
    }
}
