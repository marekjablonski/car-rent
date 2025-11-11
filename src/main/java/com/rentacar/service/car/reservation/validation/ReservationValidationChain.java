package com.rentacar.service.car.reservation.validation;

import com.rentacar.repo.CarCatalogRepository;
import com.rentacar.service.car.reservation.CreateReservationCommand;

import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
public class ReservationValidationChain {

    private final ReservationValidator chainHead;

    public ReservationValidationChain(CarCatalogRepository carCatalogRepository,
                                      Clock clock) {
        ReservationValidator identifiers = new ReservationIdentifiersValidator();
        ReservationValidator dates = identifiers.linkWith(new ReservationDatesValidator());
        ReservationValidator typeExists = dates.linkWith(new CarTypeExistsValidator(carCatalogRepository));
        typeExists.linkWith(new ReservationAvailabilityValidator(carCatalogRepository, clock));
        this.chainHead = identifiers;
    }

    public void validate(CreateReservationCommand command) {
        chainHead.validate(command);
    }
}
