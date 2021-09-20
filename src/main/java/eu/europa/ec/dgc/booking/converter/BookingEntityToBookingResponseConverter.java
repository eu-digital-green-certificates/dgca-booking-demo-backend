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
import eu.europa.ec.dgc.booking.entity.FlightInfoEntity;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
public class BookingEntityToBookingResponseConverter implements Converter<BookingEntity, BookingResponse> {

    @Override
    public BookingResponse convert(final BookingEntity entity) {
        final BookingResponse response = new BookingResponse();
        response.setReference(entity.getReference());
        response.setTime(entity.getTime());

        this.convertPassangers(entity, response);
        this.convertFlightInfo(entity, response);
        return response;
    }

    private void convertPassangers(final BookingEntity entity, final BookingResponse response) {
        final List<BookingPassengerResponse> passengerResponses = entity.getPassengers().stream()
                .map(passengerEntity -> {
                    final BookingPassengerResponse passengerResponse = new BookingPassengerResponse();
                    passengerResponse.setId(passengerEntity.getId());
                    passengerResponse.setForename(passengerEntity.getForename());
                    passengerResponse.setLastname(passengerEntity.getLastname());
                    passengerResponse.setBirthDate(passengerEntity.getBirthDate());
                    passengerResponse.setServiceIdUsed(passengerEntity.getServiceIdUsed());
                    passengerResponse.setJti(passengerEntity.getJti());

                    final DccStatusEntity dccStatusEntity = passengerEntity.getDccStatus();
                    if (dccStatusEntity != null) {
                        this.convertDccStatus(passengerResponse, dccStatusEntity);
                    }
                    return passengerResponse;
                }).collect(Collectors.toList());
        response.setPassengers(passengerResponses);
    }

    private void convertDccStatus(final BookingPassengerResponse passengerResponse,
            final DccStatusEntity dccStatusEntity) {
        final BookingPassengerDccStatusResponse dccStatusResponse = new BookingPassengerDccStatusResponse();
        dccStatusResponse.setIssuer(dccStatusEntity.getIssuer());
        dccStatusResponse.setIat(dccStatusEntity.getIat());
        dccStatusResponse.setSub(dccStatusEntity.getSub());
        dccStatusResponse.setConfirmation(dccStatusResponse.getConfirmation());

        final List<BookingPassengerDccStatusResultResponse> results = dccStatusEntity.getResults().stream()
                .map(resultEntity -> {
                    final BookingPassengerDccStatusResultResponse resultResponse = new BookingPassengerDccStatusResultResponse();
                    resultResponse.setIdentifier(resultEntity.getIdentifier());
                    resultResponse.setResult(resultEntity.getResult().name());
                    resultResponse.setType(resultEntity.getType().getName());
                    resultResponse.setDetails(resultEntity.getDetails());
                    return resultResponse;
                }).collect(Collectors.toList());
        dccStatusResponse.setResults(results);
        passengerResponse.setDccStatus(dccStatusResponse);
    }

    private void convertFlightInfo(final BookingEntity entity, final BookingResponse response) {
        final FlightInfoEntity flightInfo = entity.getFlightInfo();
        final BookingFlightInfoResponse flightInfoResponse = new BookingFlightInfoResponse();
        flightInfoResponse.setFrom(flightInfo.getFrom());
        flightInfoResponse.setTo(flightInfo.getTo());
        flightInfoResponse.setTime(flightInfo.getTime());
        flightInfoResponse.setCountryOfArrival(flightInfo.getCountryOfArrival());
        flightInfoResponse.setCountryOfDeparture(flightInfo.getCountryOfDeparture());
        flightInfoResponse.setRegionOfArrival(flightInfo.getRegionOfArrival());
        flightInfoResponse.setRegionOfDeparture(flightInfo.getRegionOfDeparture());
        flightInfoResponse.setDepartureTime(flightInfo.getDepartureTime());
        flightInfoResponse.setArrivalTime(flightInfo.getArrivalTime());
        flightInfoResponse.setType(flightInfo.getType());
        flightInfoResponse.setCategories(flightInfo.getCategories());
        flightInfoResponse.setLanguage(flightInfo.getLanguage());
        flightInfoResponse.setConditionTypes(flightInfo.getConditionTypes());
        response.setFlightInfo(flightInfoResponse);
    }
}
