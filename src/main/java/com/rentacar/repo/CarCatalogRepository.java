package com.rentacar.repo;

import com.rentacar.model.car.CarCategory;
import com.rentacar.model.car.CarType;
import com.rentacar.model.car.Car;
import com.rentacar.model.reservation.Reservation;
import com.rentacar.model.common.DateRange;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CarCatalogRepository {

    CarType saveCarType(CarType carType);

    Car registerCar(UUID carTypeId, Car car);

    Optional<CarType> findCarType(UUID id);

    Optional<Reservation> findReservation(UUID reservationId);

    Reservation saveReservation(UUID carTypeId, Reservation reservation);

    long countCarsAvailableFrom(UUID carTypeId, LocalDate pickupDate);

    long countActiveReservations(UUID carTypeId, DateRange range, Instant now);

    List<CarType> listCarTypes();

    List<CarType> filterCarTypes(Collection<CarCategory> categories);
}
