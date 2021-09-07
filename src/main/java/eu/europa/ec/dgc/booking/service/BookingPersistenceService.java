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

package eu.europa.ec.dgc.booking.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.dgc.booking.entity.BookingEntity;
import eu.europa.ec.dgc.booking.entity.BookingH2Entity;
import eu.europa.ec.dgc.booking.entity.PassengersH2Entity;
import eu.europa.ec.dgc.booking.exception.BookingNotFoundException;
import eu.europa.ec.dgc.booking.repository.BookingH2Repository;
import eu.europa.ec.dgc.booking.repository.PassengersH2Repository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingPersistenceService {

    private final BookingH2Repository bookingRepository;

    private final PassengersH2Repository passengersRepository;

    private final ObjectMapper mapper;

    /**
     * Returns booking by the session ID.
     * 
     * @param sessionId Session ID
     * @return {@link BookingEntity}
     */
    public BookingEntity getBySessionId(String sessionId) {
        BookingH2Entity entity = this.bookingRepository.findById(sessionId)
                .orElseThrow(BookingNotFoundException::new);
        return this.fromJson(entity.getBookingJson());
    }

    /**
     * Returns booking by the passenger ID.
     * 
     * @param passengerId Passenger ID as string
     * @return {@link BookingEntity}
     */
    public BookingEntity getByPassengerId(String passengerId) {
        return getByPassengerId(UUID.fromString(passengerId));
    }

    /**
     * Returns booking by the passenger ID.
     * 
     * @param passengerId Passenger ID as UUID
     * @return {@link BookingEntity}
     */
    public BookingEntity getByPassengerId(UUID passengerId) {
        PassengersH2Entity entity = passengersRepository.findById(passengerId)
                .orElseThrow(() -> new BookingNotFoundException(
                        String.format("Booking not found by passenger ID '%s'", passengerId)));
        return this.getBySessionId(entity.getSessionId());
    }

    /**
     * Saves booking , if entries already exist, they will be deleted.
     * 
     * @param sessionId Session ID
     * @param booking   Booking
     */
    public void save(String sessionId, BookingEntity booking) {
        this.cleanBySessionId(sessionId);

        BookingH2Entity entity = new BookingH2Entity();
        entity.setSessionId(sessionId);
        entity.setBookingJson(this.toJson(booking));
        bookingRepository.save(entity);

        // save passengers IDs
        booking.getPassengers().stream().map(passenger -> {
            PassengersH2Entity passEntity = new PassengersH2Entity();
            passEntity.setId(passenger.getId());
            passEntity.setSessionId(sessionId);
            return passEntity;
        }).forEach(passengersRepository::save);
    }

    private void cleanBySessionId(String sessionId) {
        if (bookingRepository.existsById(sessionId)) {
            bookingRepository.deleteById(sessionId);
        }
        if (passengersRepository.existsBySessionId(sessionId)) {
            passengersRepository.deleteAllBySessionId(sessionId);
        }
    }

    private BookingEntity fromJson(String data) {
        try {
            return mapper.readValue(data, BookingEntity.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private String toJson(BookingEntity data) {
        try {
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
