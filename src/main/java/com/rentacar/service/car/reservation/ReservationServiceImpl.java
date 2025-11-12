package com.rentacar.service.car.reservation;

import com.rentacar.model.CarType;
import com.rentacar.model.Reservation;
import com.rentacar.service.car.reservation.dto.ReservationDto;
import com.rentacar.service.car.reservation.validation.ReservationValidationChain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class ReservationServiceImpl implements ReservationService {

    static final Duration LOCK_DURATION = Duration.ofMinutes(10);
    static final Duration PAYMENT_GRACE = Duration.ofMinutes(2);

    private final CarReservationRepository carReservationRepository;
    private final ReservationValidationChain reservationValidationChain;
    private final Clock clock;

    @Override
    public ReservationDto createReservation(CreateReservationCommand command) {
        CarType carType = carReservationRepository.findCarType(command.carTypeId()).get();

        reservationValidationChain.validate(command);
        Instant now = clock.instant();
        Reservation reservation = new Reservation(
                command.reservationId(),
                command.userId(),
                carType,
                command.dateFrom(),
                command.dateTo(),
                now,
                now.plus(LOCK_DURATION)
        );
        Reservation saved = carReservationRepository.saveReservation(command.carTypeId(), reservation);
        return toDto(saved);
    }

    @Override
    public ReservationDto confirmPayment(UUID reservationId, String paymentId) {
        Reservation reservation = carReservationRepository.findReservation(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));
        Instant now = clock.instant();
        Instant paymentDeadline = reservation.lockExpiresAt().plus(PAYMENT_GRACE);
        if (now.isAfter(paymentDeadline)) {
            throw new IllegalStateException("Payment window expired for reservation: " + reservationId);
        }
        reservation.confirm(paymentId, now);
        return toDto(reservation);
    }

    @Override
    public ReservationDto getReservation(UUID reservationId) {
        return carReservationRepository.findReservation(reservationId)
                .map(ReservationServiceImpl::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));
    }

    private static ReservationDto toDto(Reservation reservation) {
        return new ReservationDto(
                reservation.id(),
                reservation.userId(),
                reservation.carType().id(),
                reservation.dateRange().start(),
                reservation.dateRange().end(),
                reservation.status(),
                reservation.lockExpiresAt(),
                reservation.paymentId()
        );
    }
}
