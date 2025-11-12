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
public class CarManagementService {

    private final CarCatalogRepository carCatalogRepository;

    public CarTypeDto registerCarType(RegisterCarTypeCommand command) {
        CarType carType = new CarType(
                command.id(),
                command.category(),
                command.pictureUrl(),
                command.pricePerDay(),
                command.seats()
        );
        CarType saved = carCatalogRepository.saveCarType(carType);
        return new CarTypeDto(
                saved.id(),
                saved.category(),
                saved.pictureUrl(),
                saved.pricePerDay(),
                saved.seats()
        );
    }

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

    public CarTypeDto getCarType(UUID carTypeId) {
        return carCatalogRepository.findCarType(carTypeId)
                .map(carType -> new CarTypeDto(
                        carType.id(),
                        carType.category(),
                        carType.pictureUrl(),
                        carType.pricePerDay(),
                        carType.seats()
                ))
                .orElseThrow(() -> new IllegalArgumentException("Car type not found: " + carTypeId));
    }
}
