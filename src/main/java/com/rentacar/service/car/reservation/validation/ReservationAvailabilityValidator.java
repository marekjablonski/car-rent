package com.rentacar.service.car.reservation.validation;

import com.rentacar.model.common.DateRange;
import com.rentacar.repo.CarCatalogRepository;
import com.rentacar.service.car.reservation.CreateReservationCommand;

import java.time.Clock;
import java.time.Instant;

class ReservationAvailabilityValidator extends ReservationValidator {

    private final CarCatalogRepository carCatalogRepository;
    private final Clock clock;

    ReservationAvailabilityValidator(CarCatalogRepository carCatalogRepository,
                                     Clock clock) {
        this.carCatalogRepository = carCatalogRepository;
        this.clock = clock;
    }

    @Override
    protected void doValidate(CreateReservationCommand command) {
        DateRange range = new DateRange(command.dateFrom(), command.dateTo());
        long totalCars = carCatalogRepository.countCarsAvailableFrom(command.carTypeId(), command.dateFrom());
        if (totalCars == 0) {
            throw new IllegalStateException("No cars of requested type are available for the selected pickup date");
        }
        Instant now = clock.instant();
        long reserved = carCatalogRepository.countActiveReservations(command.carTypeId(), range, now);
        if (reserved >= totalCars) {
            throw new IllegalStateException("Requested car type is fully booked for the selected dates");
        }
    }
}
