package com.rentacar.service.car.management;

import com.rentacar.model.CarCategory;
import com.rentacar.model.CarType;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface CarManagementRepository {

    CarType saveCarType(CarType carType);

    Optional<CarType> findCarType(UUID id);

    Set<CarType> findCarTypesByCategoryAndDates(CarCategory carCategory, LocalDate pickupDate, LocalDate dropOffDate);
}
