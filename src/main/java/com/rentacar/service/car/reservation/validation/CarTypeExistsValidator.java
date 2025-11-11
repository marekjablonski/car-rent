package com.rentacar.service.car.reservation.validation;

import com.rentacar.repo.CarCatalogRepository;
import com.rentacar.service.car.reservation.CreateReservationCommand;

class CarTypeExistsValidator extends ReservationValidator {

    private final CarCatalogRepository carCatalogRepository;

    CarTypeExistsValidator(CarCatalogRepository carCatalogRepository) {
        this.carCatalogRepository = carCatalogRepository;
    }

    @Override
    protected void doValidate(CreateReservationCommand command) {
        if (command.carTypeId() == null) {
            throw new IllegalArgumentException("carTypeId must be provided");
        }
        carCatalogRepository.findCarType(command.carTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Unknown car type: " + command.carTypeId()));
    }
}
