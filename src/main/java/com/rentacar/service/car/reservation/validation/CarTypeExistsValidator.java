package com.rentacar.service.car.reservation.validation;

import com.rentacar.service.car.reservation.CarReservationRepository;
import com.rentacar.service.car.reservation.CreateReservationCommand;

class CarTypeExistsValidator extends ReservationValidator {

    private final CarReservationRepository carReservationRepository;

    CarTypeExistsValidator(CarReservationRepository carReservationRepository) {
        this.carReservationRepository = carReservationRepository;
    }

    @Override
    protected void doValidate(CreateReservationCommand command) {
        if (command.carTypeId() == null) {
            throw new IllegalArgumentException("carTypeId must be provided");
        }
        carReservationRepository.findCarType(command.carTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Unknown car type: " + command.carTypeId()));
    }
}
