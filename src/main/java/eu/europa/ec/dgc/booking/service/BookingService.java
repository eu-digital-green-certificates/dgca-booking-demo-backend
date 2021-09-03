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

import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import eu.europa.ec.dgc.booking.dto.BookingRequest;
import eu.europa.ec.dgc.booking.dto.DevDCCStatus;
import eu.europa.ec.dgc.booking.entity.BookingEntity;
import eu.europa.ec.dgc.booking.entity.DCCStatusEntity;
import eu.europa.ec.dgc.booking.entity.PassengerEntity;
import eu.europa.ec.dgc.booking.exception.NotImplementedException;
import eu.europa.ec.dgc.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;

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

    public BookingEntity get() {
        return this.repository.get();
    }
    
    public void create(BookingRequest bookingRequest, DevDCCStatus dccStatus) {
        repository.clean();
        
        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.setReference(bookingRequest.getBookingReference());
        bookingEntity.addPassenger(PassengerEntity.build(bookingRequest));
        
        int passengersMin = dccStatus == DevDCCStatus.MIX ? 1 : passengersGeneratorMin;
        Integer numberToGenerate = new Random().nextInt(passengersGeneratorMax - passengersMin) + passengersMin;
        for (int i = 0; i < numberToGenerate; i++) {
            bookingEntity.addPassenger(PassengerEntity.random());    
        }        
        
        this.updatePassengersDccStatus(dccStatus, bookingEntity);
        
        repository.save(bookingEntity);
    }

    private void updatePassengersDccStatus(DevDCCStatus dccStatus, BookingEntity bookingEntity) {
        switch (dccStatus) {
            case FAIL:
                bookingEntity.getPassengers().forEach(pass -> pass.setDccStatus(DCCStatusEntity.failed()));
                break;
            case PASSED:
                bookingEntity.getPassengers().forEach(pass -> pass.setDccStatus(DCCStatusEntity.passed()));
                break;
            case MIX: // must be at least 2
                bookingEntity.getPassengers().forEach(pass -> pass.setDccStatus(DCCStatusEntity.passed()));
                if(bookingEntity.getPassengers().size() >= MIX_AT_LEAST) {
                    bookingEntity.getPassengers().get(0).setDccStatus(DCCStatusEntity.passed());    
                    bookingEntity.getPassengers().get(1).setDccStatus(DCCStatusEntity.failed());
                }                
                break;
            default:
                throw new NotImplementedException(); 
        }
    }
}
