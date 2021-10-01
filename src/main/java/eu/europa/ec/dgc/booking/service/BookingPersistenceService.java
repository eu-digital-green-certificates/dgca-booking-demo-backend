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
import java.util.Optional;
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
    public BookingEntity getBySessionId(final String sessionId) {
        final BookingH2Entity entity = this.bookingRepository.findById(sessionId)
                .orElseThrow(BookingNotFoundException::new);
        return this.fromJson(entity.getBookingJson());
    }

    /**
     * Returns booking by the passenger ID.
     * 
     * @param passengerId Passenger ID as string
     * @return {@link BookingEntity}
     */
    public BookingEntity getByPassengerId(final String passengerId) {
        return this.getByPassengerId(UUID.fromString(passengerId));
    }

    /**
     * Returns booking by the passenger ID.
     * 
     * @param passengerId Passenger ID as UUID
     * @return {@link BookingEntity}
     */
    public BookingEntity getByPassengerId(final UUID passengerId) {
        final String sessionId = getSessionIdByPassengerId(passengerId);
        return this.getBySessionId(sessionId);
    }

    /**
     * Returns booking by reference.
     * 
     * @param reference {@link String}
     * @return {@link BookingEntity}
     */
    public Optional<BookingEntity> getByReference(final String reference) {
        return this.bookingRepository.findByReference(reference).stream()
                .findFirst()
                .map(booking -> this.fromJson(booking.getBookingJson()));
    }

    /**
     * Returns SessionID by passenger ID.
     * 
     * @param passengerId {@link String}
     * @return Session ID
     */
    public String getSessionIdByPassengerId(final String passengerId) {
        return getSessionIdByPassengerId(UUID.fromString(passengerId));
    }

    /**
     * Returns SessionID by passenger ID.
     * 
     * @param passengerId {@link UUID}
     * @return Session ID
     */
    public String getSessionIdByPassengerId(final UUID passengerId) {
        final PassengersH2Entity entity = this.passengersRepository.findById(passengerId)
                .orElseThrow(() -> new BookingNotFoundException(
                        String.format("Booking not found by passenger ID '%s'", passengerId)));
        return entity.getSessionId();
    }

    /**
     * Saves booking , if entries already exist, they will be deleted.
     * 
     * @param sessionId Session ID
     * @param booking Booking
     */
    public void save(final String sessionId, final BookingEntity booking) {
        this.cleanBySessionId(sessionId);

        final BookingH2Entity entity = new BookingH2Entity();
        entity.setSessionId(sessionId);
        entity.setReference(booking.getReference());
        entity.setBookingJson(this.toJson(booking));
        this.bookingRepository.save(entity);

        // save passengers IDs
        booking.getPassengers().stream().map(passenger -> {
            final PassengersH2Entity passEntity = new PassengersH2Entity();
            passEntity.setId(passenger.getId());
            passEntity.setSessionId(sessionId);
            return passEntity;
        }).forEach(passengersRepository::save);
    }

    public void deleteByReference(final String reference) {
        this.bookingRepository.deleteByReference(reference);
    }

    private void cleanBySessionId(final String sessionId) {
        if (this.bookingRepository.existsById(sessionId)) {
            this.bookingRepository.deleteById(sessionId);
        }
        if (this.passengersRepository.existsBySessionId(sessionId)) {
            this.passengersRepository.deleteAllBySessionId(sessionId);
        }
    }

    private BookingEntity fromJson(final String data) {
        try {
            return this.mapper.readValue(data, BookingEntity.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private String toJson(final BookingEntity data) {
        try {
            return this.mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
