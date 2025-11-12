package com.rentacar.service.car.reservation.validation;

import com.rentacar.service.car.reservation.CarReservationRepository;
import com.rentacar.service.car.reservation.CreateReservationCommand;
import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
public class ReservationValidationChain {

    private final ReservationValidator chainHead;

    public ReservationValidationChain(CarReservationRepository carReservationRepository,
                                      Clock clock) {
        ReservationValidator identifiers = new ReservationIdentifiersValidator();
        ReservationValidator dates = identifiers.linkWith(new ReservationDatesValidator());
        ReservationValidator typeExists = dates.linkWith(new CarTypeExistsValidator(carReservationRepository));
        typeExists.linkWith(new ReservationAvailabilityValidator(carReservationRepository, clock));
        this.chainHead = identifiers;
    }

    public void validate(CreateReservationCommand command) {
        chainHead.validate(command);
    }
}
