package com.rentacar.service.car.reservation;

import com.rentacar.model.CarCategory;
import com.rentacar.model.CarType;
import com.rentacar.model.DateRange;
import com.rentacar.model.Reservation;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CarReservationRepository {

    Optional<CarType> findCarType(UUID id);

    Optional<Reservation> findReservation(UUID reservationId);

    Reservation saveReservation(UUID carTypeId, Reservation reservation);

    long countCarsAvailableFrom(UUID carTypeId, LocalDate pickupDate);

    long countActiveReservations(UUID carTypeId, DateRange range, Instant now);

    List<CarType> filterCarTypes(Collection<CarCategory> categories);
}
