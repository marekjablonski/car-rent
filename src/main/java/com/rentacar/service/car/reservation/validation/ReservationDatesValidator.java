package com.rentacar.service.car.reservation.validation;

import com.rentacar.service.car.reservation.CreateReservationCommand;

import java.time.LocalDate;

class ReservationDatesValidator extends ReservationValidator {

    @Override
    protected void doValidate(CreateReservationCommand command) {
        LocalDate from = command.dateFrom();
        LocalDate to = command.dateTo();
        if (from == null || to == null) {
            throw new IllegalArgumentException("Both dateFrom and dateTo must be provided");
        }
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("dateTo must be on or after dateFrom");
        }
    }
}
