package com.rentacar.service.car.reservation.validation;

import com.rentacar.service.car.reservation.CreateReservationCommand;

public abstract class ReservationValidator {

    private ReservationValidator next;

    public ReservationValidator linkWith(ReservationValidator nextValidator) {
        this.next = nextValidator;
        return nextValidator;
    }

    public final void validate(CreateReservationCommand command) {
        doValidate(command);
        if (next != null) {
            next.validate(command);
        }
    }

    protected abstract void doValidate(CreateReservationCommand command);
}
