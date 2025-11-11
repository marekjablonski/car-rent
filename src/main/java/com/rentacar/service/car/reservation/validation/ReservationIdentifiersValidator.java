package com.rentacar.service.car.reservation.validation;

import com.rentacar.service.car.reservation.CreateReservationCommand;

class ReservationIdentifiersValidator extends ReservationValidator {

    @Override
    protected void doValidate(CreateReservationCommand command) {
        if (command.reservationId() == null) {
            throw new IllegalArgumentException("reservationId must be provided");
        }
        if (command.userId() == null) {
            throw new IllegalArgumentException("userId must be provided");
        }
    }
}
