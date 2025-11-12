package com.rentacar.service.car.reservation.validation;

import com.rentacar.model.DateRange;
import com.rentacar.service.car.reservation.CarReservationRepository;
import com.rentacar.service.car.reservation.CreateReservationCommand;

import java.time.Clock;
import java.time.Instant;

class ReservationAvailabilityValidator extends ReservationValidator {

    private final CarReservationRepository carReservationRepository;
    private final Clock clock;

    ReservationAvailabilityValidator(CarReservationRepository carReservationRepository,
                                     Clock clock) {
        this.carReservationRepository = carReservationRepository;
        this.clock = clock;
    }

    @Override
    protected void doValidate(CreateReservationCommand command) {
        DateRange range = new DateRange(command.dateFrom(), command.dateTo());
        long totalCars = carReservationRepository.countCarsAvailableFrom(command.carTypeId(), command.dateFrom());
        if (totalCars == 0) {
            throw new IllegalStateException("No cars of requested type are available for the selected pickup date");
        }
        Instant now = clock.instant();
        long reserved = carReservationRepository.countActiveReservations(command.carTypeId(), range, now);
        if (reserved >= totalCars) {
            throw new IllegalStateException("Requested car type is fully booked for the selected dates");
        }
    }
}
