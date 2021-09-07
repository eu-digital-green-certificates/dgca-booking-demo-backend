/*-
 * ---license-start
 * European Digital COVID Certificate Booking Demo / dgca-booking-demo-backend
 * ---
 * Copyright (C) 2021 T-Systems International GmbH and all other contributors
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ---license-end
 */

package eu.europa.ec.dgc.booking.entity;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class BookingEntity {

    private String reference;

    private OffsetDateTime time = OffsetDateTime.now();

    private List<PassengerEntity> passengers = new ArrayList<>();

    private FlightInfoEntity flightInfo = FlightInfoEntity.random();

    /**
     * Constructor. Set current timestamp (now) to "time". Set random "flightInfo".
     */
    public BookingEntity() {
    }

    /**
     * Add PassengerEntity to current {@link List}.
     * 
     * @param passenger {@link PassengerEntity}
     */
    public void addPassenger(PassengerEntity passenger) {
        this.passengers.add(passenger);
    }

    /**
     * Delivers a passenger by ID if this exists.
     * 
     * @param passengerId ID as string
     * @return {@link Optional} of {@link PassengerEntity}
     */
    public Optional<PassengerEntity> getPassengerById(String passengerId) {
        try {
            return getPassengerById(UUID.fromString(passengerId));
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Delivers a passenger by ID if this exists.
     * 
     * @param passengerId ID as UUID
     * @return {@link Optional} of {@link PassengerEntity}
     */
    public Optional<PassengerEntity> getPassengerById(UUID passengerId) {
        return this.passengers.stream()
        .filter(passenger -> passenger.getId().equals(passengerId))
                .findAny();
    }
}
