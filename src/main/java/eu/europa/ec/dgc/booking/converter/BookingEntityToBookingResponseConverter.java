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

package eu.europa.ec.dgc.booking.converter;


import eu.europa.ec.dgc.booking.dto.BookingResponse;
import eu.europa.ec.dgc.booking.dto.BookingResponse.BookingFlightInfoResponse;
import eu.europa.ec.dgc.booking.dto.BookingResponse.BookingPassengerDccStatusResponse;
import eu.europa.ec.dgc.booking.dto.BookingResponse.BookingPassengerDccStatusResultResponse;
import eu.europa.ec.dgc.booking.dto.BookingResponse.BookingPassengerResponse;
import eu.europa.ec.dgc.booking.entity.BookingEntity;
import eu.europa.ec.dgc.booking.entity.DccStatusEntity;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
public class BookingEntityToBookingResponseConverter implements Converter<BookingEntity, BookingResponse> {

    @Override
    public BookingResponse convert(BookingEntity entity) {
        BookingResponse response = new BookingResponse();
        response.setReference(entity.getReference());
        response.setSubject(entity.getReference());
        response.setTime(entity.getTime());
        
        this.convertPassangers(entity, response);        
        this.convertFlightInfo(entity, response);        
        return response;
    }

    private void convertPassangers(BookingEntity entity, BookingResponse response) {
        List<BookingPassengerResponse> passengerResponses = entity.getPassengers().stream().map(passengerEntity -> {
            BookingPassengerResponse passengerResponse = new BookingPassengerResponse();
            passengerResponse.setId(passengerEntity.getId());
            passengerResponse.setForename(passengerEntity.getForename());
            passengerResponse.setLastname(passengerEntity.getLastname());
            
            DccStatusEntity dccStatusEntity = passengerEntity.getDccStatus();
            if (dccStatusEntity != null) {
                this.convertDccStatus(passengerResponse, dccStatusEntity);
            }
            return passengerResponse;
        }).collect(Collectors.toList());
        response.setPassengers(passengerResponses);
    }

    private void convertDccStatus(BookingPassengerResponse passengerResponse, DccStatusEntity dccStatusEntity) {
        BookingPassengerDccStatusResponse dccStatusResponse = new BookingPassengerDccStatusResponse();
        dccStatusResponse.setIssuer(dccStatusEntity.getIssuer());
        dccStatusResponse.setIat(dccStatusEntity.getIat());
        dccStatusResponse.setSub(dccStatusEntity.getSub());
        dccStatusResponse.setConfirmation(dccStatusResponse.getConfirmation());
                        
        List<BookingPassengerDccStatusResultResponse> results = dccStatusEntity.getResults().stream()
                .map(resultEntity -> {
                    BookingPassengerDccStatusResultResponse resultResponse = 
                            new BookingPassengerDccStatusResultResponse();
                    resultResponse.setIdentifier(resultEntity.getIdentifier());
                    resultResponse.setResult(resultEntity.getResult().name());
                    resultResponse.setType(resultEntity.getType().getName());
                    resultResponse.setDetails(resultEntity.getDetails());
                    return resultResponse;
                }).collect(Collectors.toList());
        dccStatusResponse.setResults(results);
        passengerResponse.setDssStatus(dccStatusResponse);
    }

    private void convertFlightInfo(BookingEntity entity, BookingResponse response) {
        BookingFlightInfoResponse flightInfoResponse = new BookingFlightInfoResponse();
        flightInfoResponse.setFrom(entity.getFlightInfo().getFrom());
        flightInfoResponse.setTo(entity.getFlightInfo().getTo());
        flightInfoResponse.setTime(entity.getFlightInfo().getTime());
        response.setFlightInfo(flightInfoResponse);
    }
}
