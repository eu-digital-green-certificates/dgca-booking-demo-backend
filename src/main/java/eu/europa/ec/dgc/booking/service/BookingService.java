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

import eu.europa.ec.dgc.booking.dto.BookingRequest;
import eu.europa.ec.dgc.booking.dto.DevDccStatus;
import eu.europa.ec.dgc.booking.dto.ResultStatusRequest;
import eu.europa.ec.dgc.booking.entity.BookingEntity;
import eu.europa.ec.dgc.booking.entity.DccStatusEntity;
import eu.europa.ec.dgc.booking.entity.PassengerEntity;
import eu.europa.ec.dgc.booking.exception.BookingNotFoundException;
import eu.europa.ec.dgc.booking.exception.NotImplementedException;
import eu.europa.ec.dgc.booking.repository.BookingRepository;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

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

    private final BookingRepository repository;

    private final ConversionService converter;

    /**
     * Return current BookingEntity from session.
     * 
     * @return {@link BookingEntity}
     */
    public BookingEntity get() {
        return this.repository.get();
    }

    /**
     * Return current BookingEntity from session by Subject.
     * 
     * @param subject Subject
     * @return {@link BookingEntity}
     */
    public BookingEntity getBySubject(String subject) {
        BookingEntity bookingEntity = this.get();
        if (bookingEntity.getSubject().equals(subject)) {
            return bookingEntity;
        }
        throw new BookingNotFoundException();
    }

    /**
     * Create and write BookingEntity to session. if an entry already exists, it will be deleted.
     * 
     * @param bookingRequest data from the frontend
     * @param dccStatus      status manipulation for test purposes
     */
    public void create(BookingRequest bookingRequest, DevDccStatus dccStatus) {
        repository.clean();

        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.setReference(bookingRequest.getBookingReference());
        bookingEntity.addPassenger(PassengerEntity.build(bookingRequest));

        int passengersMin = dccStatus == DevDccStatus.MIX ? 1 : passengersGeneratorMin;
        Integer numberToGenerate = new Random().nextInt(passengersGeneratorMax - passengersMin) + passengersMin;
        for (int i = 0; i < numberToGenerate; i++) {
            bookingEntity.addPassenger(PassengerEntity.random());
        }

        this.updatePassengersDccStatus(dccStatus, bookingEntity);

        repository.save(bookingEntity);
    }

    /**
     * Checks whether DCC status is set for all passengers.
     * 
     * @return {@link Boolean}
     */
    public boolean existsDcc() {
        return this.get().getPassengers().stream()
                .allMatch(passenger -> passenger.getDccStatus() != null);
    }

    /**
     * Updates the DCC status for all passengers.
     * 
     * @param subject       Subject
     * @param resultRequest received result
     * @return Number of changed passengers
     */
    public int updateResult(final String subject, final ResultStatusRequest resultRequest) {
        return updateResult(subject, null, resultRequest);
    }

    /**
     * Updates the DCC status for one passenger by ID.
     * 
     * @param subject       Subject
     * @param passengerId   passenger ID
     * @param resultRequest received result
     * @return Number of changed passengers
     */
    public int updateResult(final String subject, final String passengerId, final ResultStatusRequest resultRequest) {
        final BookingEntity bookingEntity = getBySubject(subject);
        final List<PassengerEntity> passengerForUpdate = bookingEntity.getPassengers().stream()
                .filter(passenger -> passengerId == null || passengerId.equals(passenger.getId().toString()))
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
