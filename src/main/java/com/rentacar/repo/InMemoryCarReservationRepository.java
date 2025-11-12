package com.rentacar.repo;

import com.rentacar.model.*;
import com.rentacar.service.car.management.CarManagementRepository;
import com.rentacar.service.car.reservation.CarReservationRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryCarReservationRepository implements CarReservationRepository, CarManagementRepository {

    private final Map<UUID, CarType> carTypes = new ConcurrentHashMap<>();
    private final Map<CarCategory, UUID> typePerCategory = new ConcurrentHashMap<>();
    private final Map<String, UUID> plateIndex = new ConcurrentHashMap<>();
    private final Map<UUID, UUID> reservationIndex = new ConcurrentHashMap<>();

    @Override
    public CarType saveCarType(CarType carType) {
        CarType previous = carTypes.putIfAbsent(carType.id(), carType);
        if (previous != null) {
            throw new IllegalArgumentException("Car type with id %s already exists".formatted(carType.id()));
        }
        UUID existingTypeForCategory = typePerCategory.putIfAbsent(carType.category(), carType.id());
        if (existingTypeForCategory != null) {
            carTypes.remove(carType.id());
            throw new IllegalArgumentException("Car type for category %s already configured".formatted(carType.category()));
        }
        return carType;
    }

    @Override
    public Car registerCar(UUID carTypeId, Car car) {
        CarType carType = getCarTypeOrThrow(carTypeId);
        UUID previousPlate = plateIndex.putIfAbsent(car.numberPlate(), car.id());
        if (previousPlate != null) {
            throw new IllegalArgumentException("Number plate already registered: " + car.numberPlate());
        }
        synchronized (carType) {
            carType.addCar(car);
        }
        return car;
    }

    @Override
    public Optional<CarType> findCarType(UUID id) {
        return Optional.ofNullable(carTypes.get(id));
    }

    @Override
    public Set<CarType> findCarTypesByCategoryAndDates(CarCategory carCategory, LocalDate pickupDate, LocalDate dropOffDate) {
        return Set.of();
    }

    @Override
    public Optional<Reservation> findReservation(UUID reservationId) {
        UUID carTypeId = reservationIndex.get(reservationId);
        if (carTypeId == null) {
            return Optional.empty();
        }
        CarType carType = carTypes.get(carTypeId);
        if (carType == null) {
            reservationIndex.remove(reservationId);
            return Optional.empty();
        }
        synchronized (carType) {
            return carType.findReservation(reservationId);
        }
    }

    @Override
    public Reservation saveReservation(UUID carTypeId, Reservation reservation) {
        CarType carType = getCarTypeOrThrow(carTypeId);
        synchronized (carType) {
            carType.addReservation(reservation);
            reservationIndex.put(reservation.id(), carTypeId);
        }
        return reservation;
    }

    @Override
    public long countCarsAvailableFrom(UUID carTypeId, LocalDate pickupDate) {
        CarType carType = getCarTypeOrThrow(carTypeId);
        synchronized (carType) {
            return carType.countCarsAvailableFrom(pickupDate);
        }
    }

    @Override
    public long countActiveReservations(UUID carTypeId, DateRange range, Instant now) {
        CarType carType = getCarTypeOrThrow(carTypeId);
        synchronized (carType) {
            return carType.countActiveReservations(range, now);
        }
    }

    private CarType getCarTypeOrThrow(UUID id) {
        return Objects.requireNonNull(carTypes.get(id), "Car type not found: " + id);
    }
}
