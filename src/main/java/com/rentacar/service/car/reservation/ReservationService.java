package com.rentacar.service.car.reservation;

import com.rentacar.repo.CarCatalogRepository;
import com.rentacar.model.reservation.Reservation;
import com.rentacar.service.car.reservation.validation.ReservationValidationChain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {

    static final Duration LOCK_DURATION = Duration.ofMinutes(10);
    static final Duration PAYMENT_GRACE = Duration.ofMinutes(2);

    private final CarCatalogRepository carCatalogRepository;
    private final ReservationValidationChain reservationValidationChain;
    private final Clock clock;

    public Reservation createReservation(CreateReservationCommand command) {
        reservationValidationChain.validate(command);
        Instant now = clock.instant();
        Reservation reservation = new Reservation(
                command.reservationId(),
                command.userId(),
                command.carTypeId(),
                command.dateFrom(),
                command.dateTo(),
                now,
                now.plus(LOCK_DURATION)
        );
        return carCatalogRepository.saveReservation(command.carTypeId(), reservation);
    }

    public Reservation confirmPayment(UUID reservationId, String paymentId) {
        Reservation reservation = carCatalogRepository.findReservation(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));
        Instant now = clock.instant();
        Instant paymentDeadline = reservation.lockExpiresAt().plus(PAYMENT_GRACE);
        if (now.isAfter(paymentDeadline)) {
            throw new IllegalStateException("Payment window expired for reservation: " + reservationId);
        }
        reservation.confirm(paymentId, now);
        return reservation;
    }
}
