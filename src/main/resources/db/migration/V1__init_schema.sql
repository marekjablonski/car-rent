CREATE TABLE car_types
(
    id            UUID PRIMARY KEY,
    category      VARCHAR(32)    NOT NULL,
    picture_url   TEXT           NOT NULL,
    price_per_day NUMERIC(12, 2) NOT NULL CHECK (price_per_day > 0),
    seats         INTEGER        NOT NULL CHECK (seats > 0)
);

CREATE TABLE cars
(
    id             UUID PRIMARY KEY,
    car_type_id    UUID        NOT NULL REFERENCES car_types (id) ON DELETE CASCADE,
    number_plate   VARCHAR(32) NOT NULL UNIQUE,
    available_from DATE        NOT NULL
);

CREATE INDEX ix_cars_car_type_available_from ON cars (car_type_id, available_from);

CREATE TABLE reservations
(
    id              UUID PRIMARY KEY,
    car_type_id     UUID        NOT NULL REFERENCES car_types (id) ON DELETE CASCADE,
    user_id         UUID        NOT NULL,
    date_from       DATE        NOT NULL,
    date_to         DATE        NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL,
    lock_expires_at TIMESTAMPTZ NOT NULL,
    status          VARCHAR(32) NOT NULL,
    payment_id      VARCHAR(64),
    confirmed_at    TIMESTAMPTZ
);

ALTER TABLE reservations
    ADD CONSTRAINT chk_reservation_dates CHECK (date_to >= date_from);

CREATE INDEX ix_reservations_type_dates ON reservations (car_type_id, date_from, date_to);
CREATE INDEX ix_reservations_status ON reservations (status);
