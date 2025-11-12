package com.rentacar.service.car.management;

import com.rentacar.model.Car;
import com.rentacar.model.CarType;
import com.rentacar.repo.CarCatalogRepository;
import com.rentacar.service.car.management.dto.CarDto;
import com.rentacar.service.car.management.dto.CarTypeDto;
import com.rentacar.service.car.management.dto.RegisterCarCommand;
import com.rentacar.service.car.management.dto.RegisterCarTypeCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class CarManagementServiceImpl implements CarManagementService {

    private final CarCatalogRepository carCatalogRepository;

    @Override
    public CarTypeDto registerCarType(RegisterCarTypeCommand command) {
        CarType carType = new CarType(
                command.id(),
                command.category(),
                command.pictureUrl(),
                command.pricePerDay(),
                command.seats()
        );
        CarType saved = carCatalogRepository.saveCarType(carType);
        return toCarTypeDto(saved);
    }

    @Override
    public CarDto registerCar(RegisterCarCommand command) {
        Car car = new Car(
                UUID.randomUUID(),
                command.carTypeId(),
                command.numberPlate().toUpperCase(Locale.ROOT),
                command.availableFrom()
        );
        Car saved = carCatalogRepository.registerCar(command.carTypeId(), car);
        return new CarDto(
                saved.id(),
                saved.carTypeId(),
                saved.numberPlate(),
                saved.availableFrom()
        );
    }

    @Override
    public CarTypeDto getCarType(UUID carTypeId) {
        return carCatalogRepository.findCarType(carTypeId)
                .map(CarManagementServiceImpl::toCarTypeDto)
                .orElseThrow(() -> new IllegalArgumentException("Car type not found: " + carTypeId));
    }

    private static CarTypeDto toCarTypeDto(CarType carType) {
        return new CarTypeDto(
                carType.id(),
                carType.category(),
                carType.pictureUrl(),
                carType.pricePerDay(),
                carType.seats()
        );
    }
}
