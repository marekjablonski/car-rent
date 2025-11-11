# Car Rental Reservation System

Spring Boot service that manages a limited fleet of three car classes (Sedan, SUV, Van) and exposes endpoints for
creating cars, listing available car types, and booking reservations that span whole days.

## Functional Requirements

- Reserve a car by type for a chosen pickup date, time, and rental duration (in days). Users always book a *type*, never
  an individual car.
- Each car type has a finite inventory; overlapping reservations cannot exceed the available count.
- Reservations lock inventory for 10 minutes while the user pays. Slight delays are acceptable due to scheduler
  synchronization.
- Car availability, and payment confirmation must be verifiable through unit tests.

## Non-Functional Requirements

- Low QPS scenario, so no queuing or heavy autoscaling is required.
- User management is out of scope; user IDs are assumed to be provided by an external system.
- Majority of validation via spring-boot-starter-validation annotations in the service layer.
- Usage of lombok annotations whenever possible. Entities equals method based on id.

## Domain Model

- **CarType** – Aggregate Root - Describes a category (Sedan/SUV/Van), photo, seats, and price per day, set\<Car> cars, set\<Reservation> reservations.
- **Car** – inside CarType—oneToMany Relation- Individual vehicle tied to a `CarType`, identified by its plate number, with an availability
  start date.
- **Reservation** –inside CarType - Holds user, car type, date range, and status. Confirmed only when payment arrives
  within the lock window.

## Application Layers
Layered MVC Architecture is more suitable for CRUD applications
com.rentacar
├─ web/                # controllers, request/response DTOs   
│   ├─ car.management  # creates carType and car   
│   ├─ car.reservation # car reservation  
├─ service/            # business logic (uses entities)    
│   ├─ car.management  # creates carType and car  
│   ├─ car.reservation # car reservation  
├─ repo/               # repos   
└─ model/              # models and mappers (entity <-> DTO)  

## API Contract

```http
POST /carType
{
  "id": "uuid",
  "type": "Sedan",
  "pictureUrl": "https://not.exist/car/photos/200/300",
  "pricePerDay": 100,
  "seats": 5
}

POST /car
{
  "carTypeId": "uuid",
  "numberPlate": "ABC123",
  "availableFrom": "2026-01-01"
}

GET /carType?type=[Sedan,SUV]&pickup-date=2025-11-15&drop-off-date=2025-11-22
[
  {
    "type": "Sedan",
    "pictureUrl": "https://not.exist/car/photos/200/300",
    "pricePerDay": 100,
    "seats": 5
  }
]

POST /reservation
{
  "reservationId": "uuid",
  "userId": "uuid",
  "carTypeId": "uuid",
  "dateFrom": "2026-01-01",
  "dateTo": "2026-01-03"
}

POST /payment
{
  "reservationId": "uuid",
  "paymentId": "uuid"
}
```

## Running Locally

```bash
./gradlew bootRun
```

The service listens on port 8080 by default; adjust `src/main/resources/application.properties` for a different profile
or port.

## Tests

```bash
./gradlew test
```

Unit tests must cover inventory limits, overlapping reservation handling, and the payment lock window. Add scenarios
whenever new constraints are introduced so that the suite continues to prove the stated requirements.
