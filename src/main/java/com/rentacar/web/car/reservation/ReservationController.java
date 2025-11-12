package com.rentacar.web.car.reservation;

import com.rentacar.service.car.reservation.CreateReservationCommand;
import com.rentacar.service.car.reservation.ReservationService;
import com.rentacar.service.car.reservation.dto.ReservationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/reservation")
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody CreateReservationRequest request) {
        UUID reservationId = request.reservationId() != null ? request.reservationId() : UUID.randomUUID();
        CreateReservationCommand command = new CreateReservationCommand(
                reservationId,
                request.userId(),
                request.carTypeId(),
                request.dateFrom(),
                request.dateTo()
        );
        ReservationResponse body = toResponse(reservationService.createReservation(command));
        return ResponseEntity.created(URI.create("/reservation/" + body.reservationId())).body(body);
    }

    @PostMapping("/payment")
    public ReservationResponse confirmPayment(@RequestBody PaymentRequest request) {
        if (request.reservationId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "reservationId is required");
        }
        if (request.paymentId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "paymentId is required");
        }
        return toResponse(reservationService.confirmPayment(request.reservationId(), request.paymentId().toString()));
    }

    @GetMapping("/reservation/{reservationId}")
    public ReservationResponse getReservation(@PathVariable UUID reservationId) {
        return toResponse(reservationService.getReservation(reservationId));
    }

    private static ReservationResponse toResponse(ReservationDto reservation) {
        return new ReservationResponse(
                reservation.reservationId(),
                reservation.userId(),
                reservation.carTypeId(),
                reservation.dateFrom(),
                reservation.dateTo(),
                reservation.status(),
                reservation.lockExpiresAt(),
                reservation.paymentId(),
                reservationLinks(reservation.reservationId(), reservation.carTypeId())
        );
    }

    private static Map<String, String> reservationLinks(UUID reservationId, UUID carTypeId) {
        return Map.of(
                "self", "/reservation/" + reservationId,
                "payment", "/payment",
                "carType", "/carType/" + carTypeId
        );
    }
}
