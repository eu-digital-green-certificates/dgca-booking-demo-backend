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

import eu.europa.ec.dgc.booking.dto.BookingReplaceRequest;
import eu.europa.ec.dgc.booking.dto.BookingRequest;
import eu.europa.ec.dgc.booking.dto.DevDccStatus;
import eu.europa.ec.dgc.booking.dto.ResultStatusRequest;
import eu.europa.ec.dgc.booking.entity.BookingEntity;
import eu.europa.ec.dgc.booking.entity.DccStatusEntity;
import eu.europa.ec.dgc.booking.entity.PassengerEntity;
import eu.europa.ec.dgc.booking.exception.BookingNotFoundException;
import eu.europa.ec.dgc.booking.exception.NotImplementedException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private static final int MIX_AT_LEAST = 2;

    // Number of additional passengers to be created for the booking reference
    @Value("${demo.passengers.generator.min:0}")
    private Integer passengersGeneratorMin;

    // Number of additional passengers to be created for the booking reference
    @Value("${demo.passengers.generator.max:2}")
    private Integer passengersGeneratorMax;

    // Randomly create passengers or use a list of preconfigured
    @Value("${demo.passengers.random:false}")
    private Boolean passengersRandom;

    private final BookingPersistenceService persistence;

    private final ConversionService converter;

    /**
     * Return BookingEntity by session ID.
     * 
     * @return {@link BookingEntity}
     */
    public BookingEntity getBySessionId(final String sessionId) {
        return this.persistence.getBySessionId(sessionId);
    }

    /**
     * Return BookingEntity by passenger ID.
     * 
     * @param passengerId Passenger ID
     * @return {@link BookingEntity}
     */
    public BookingEntity getByPassengerId(final String passengerId) {
        return this.persistence.getByPassengerId(passengerId);
    }

    /**
     * Return BookingEntity by reference.
     * 
     * @param reference {@link String}
     * @return {@link BookingEntity}
     */
    public BookingEntity getByReference(final String reference) {
        return this.persistence.getByReference(reference)
                .orElseThrow(() -> new BookingNotFoundException(
                        String.format("Booking not found by reference '%s'", reference)));
    }

    /**
     * Return current BookingEntity if passenger ID is found and limits the content to the passenger with the ID.
     * 
     * @param passengerId Passenger ID
     * @param serviceId Service ID (optional)
     * @return {@link BookingEntity}
     */
    public BookingEntity getOnlyPassengerId(final String passengerId, final String serviceId) {
        final BookingEntity bookingEntity = this.getByPassengerId(passengerId);
        final PassengerEntity passenger = bookingEntity.getPassengerById(passengerId)
                .orElseThrow(() -> new BookingNotFoundException(
                        String.format("Booking not found by passenger ID '%s'", passengerId)));

        if (serviceId != null && !serviceId.isBlank()) {
            passenger.setServiceIdUsed(serviceId);
            this.persistence.save(this.persistence.getSessionIdByPassengerId(passengerId), bookingEntity);
        }

        bookingEntity.setPassengers(Arrays.asList(passenger));
        return bookingEntity;
    }

    /**
     * Create and write BookingEntity to session. if an entry already exists, it will be deleted.
     * 
     * @param sessionId current Session ID
     * @param request data from the frontend
     * @param dccStatus status manipulation for test purposes
     */
    public void create(final String sessionId, final BookingRequest request, final DevDccStatus dccStatus) {
        final Optional<BookingEntity> entity = this.persistence.getByReference(request.getBookingReference());
        final boolean isPreset = entity.isPresent()
                && request.getBookingReference() != null
                && request.getBookingReference().startsWith("preset");
        if (!isPreset) {
            log.info("Create new BookingEntity for reference '{}'", request.getBookingReference());
            final BookingEntity bookingEntity = new BookingEntity();
            bookingEntity.setReference(request.getBookingReference());
            bookingEntity.addPassenger(PassengerEntity.build(request));

            int passengersMin = dccStatus == DevDccStatus.MIX ? 1 : this.passengersGeneratorMin;
            Integer numberToGenerate = new Random().nextInt(this.passengersGeneratorMax - passengersMin)
                    + passengersMin;
            for (int i = 0; i < numberToGenerate; i++) {
                if (this.passengersRandom) {
                    bookingEntity.addPassenger(PassengerEntity.random());
                } else {
                    bookingEntity.addPassenger(PassengerEntity.immutable(i));
                }
            }

            this.updatePassengersDccStatus(dccStatus, bookingEntity);
            this.persistence.save(sessionId, bookingEntity);
        } else {
            log.info("Use existing BookingEntity by reference '{}'", request.getBookingReference());
        }
    }

    /**
     * Replaces or creates a {@link BookingEntity}.
     * 
     * @param sessionId Session ID
     * @param request {@link BookingReplaceRequest}
     * @return {@link BookingEntity}
     */
    public BookingEntity replace(final String sessionId, final BookingReplaceRequest request) {
        final BookingEntity entity = this.converter.convert(request, BookingEntity.class);
        this.persistence.deleteByReference(request.getReference());
        this.persistence.save(sessionId, entity);
        return entity;
    }

    /**
     * Checks whether DCC status is set for all passengers.
     * 
     * @return {@link Boolean}
     */
    public boolean existsDccBySessionId(String sessionId) {
        return this.getBySessionId(sessionId).getPassengers().stream()
                .allMatch(passenger -> passenger.getDccStatus() != null);
    }

    /**
     * Updates the DCC status for one passenger by ID.
     * 
     * @param passengerId passenger ID
     * @param resultRequest received result
     * @return Number of changed passengers
     */
    public int updateResult(final String passengerId, final ResultStatusRequest resultRequest) {
        final BookingEntity bookingEntity = this.getByPassengerId(passengerId);
        final List<PassengerEntity> passengerForUpdate = bookingEntity.getPassengers().stream()
                .filter(passenger -> passenger.getId().toString().equals(passengerId))
                .collect(Collectors.toList());

        passengerForUpdate.forEach(passenger -> {
            final DccStatusEntity dccStatusEntity = converter.convert(resultRequest, DccStatusEntity.class);
            passenger.setDccStatus(dccStatusEntity);
        });
        return passengerForUpdate.size();
    }

    private void updatePassengersDccStatus(DevDccStatus dccStatus, BookingEntity bookingEntity) {
        if (dccStatus != null) {
            switch (dccStatus) {
                case FAIL:
                    bookingEntity.getPassengers().forEach(pass -> pass.setDccStatus(DccStatusEntity.failed()));
                    break;
                case PASSED:
                    bookingEntity.getPassengers().forEach(pass -> pass.setDccStatus(DccStatusEntity.passed()));
                    break;
                case MIX: // must be at least 2
                    bookingEntity.getPassengers().forEach(pass -> pass.setDccStatus(DccStatusEntity.passed()));
                    if (bookingEntity.getPassengers().size() >= MIX_AT_LEAST) {
                        bookingEntity.getPassengers().get(0).setDccStatus(DccStatusEntity.passed());
                        bookingEntity.getPassengers().get(1).setDccStatus(DccStatusEntity.failed());
                    }
                    break;
                default:
                    throw new NotImplementedException();
            }
        }
    }
}
