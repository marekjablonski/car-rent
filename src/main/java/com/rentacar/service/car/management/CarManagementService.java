package com.rentacar.service.car.management;

import com.rentacar.repo.CarCatalogRepository;
import com.rentacar.model.car.Car;
import com.rentacar.model.car.CarType;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CarManagementService {

    private final CarCatalogRepository carCatalogRepository;

    public CarType registerCarType(CarType carType) {
        return carCatalogRepository.saveCarType(carType);
    }

    public Car registerCar(UUID carTypeId, String numberPlate, LocalDate availableFrom) {
        Car car = new Car(
                UUID.randomUUID(),
                carTypeId,
                numberPlate.toUpperCase(Locale.ROOT),
                availableFrom
        );
        return carCatalogRepository.registerCar(carTypeId, car);
    }
}
