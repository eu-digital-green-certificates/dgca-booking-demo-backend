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

import eu.europa.ec.dgc.booking.dto.BookingReplaceRequest;
import eu.europa.ec.dgc.booking.dto.BookingReplaceRequest.BookingFlightInfoRequest;
import eu.europa.ec.dgc.booking.dto.BookingReplaceRequest.DccStatusRequest;
import eu.europa.ec.dgc.booking.dto.BookingReplaceRequest.PassengerRequest;
import eu.europa.ec.dgc.booking.dto.BookingReplaceRequest.ResultRequest;
import eu.europa.ec.dgc.booking.entity.BookingEntity;
import eu.europa.ec.dgc.booking.entity.DccStatusEntity;
import eu.europa.ec.dgc.booking.entity.DccStatusResultEntity;
import eu.europa.ec.dgc.booking.entity.FlightInfoEntity;
import eu.europa.ec.dgc.booking.entity.PassengerEntity;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
public class BookingReplaceRequestToBookingEntityConverter implements Converter<BookingReplaceRequest, BookingEntity> {

    @Override
    public BookingEntity convert(BookingReplaceRequest request) {
        final BookingEntity entity = new BookingEntity();
        entity.setReference(request.getReference());
        entity.setTime(request.getTime() != null ? request.getTime() : OffsetDateTime.now());
        entity.setPassengers(this.convertPassengers(request.getPassengers()));
        entity.setFlightInfo(this.convertFlightInfo(request.getFlightInfo()));
        return entity;
    }

    private List<PassengerEntity> convertPassengers(List<PassengerRequest> requests) {
        if (requests != null && !requests.isEmpty()) {
            return requests.stream()
                    .map(this::convertPassenger)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private PassengerEntity convertPassenger(PassengerRequest request) {
        final PassengerEntity entity = new PassengerEntity();
        entity.setId(request.getId() != null ? request.getId() : UUID.randomUUID());
        entity.setForename(request.getForename());
        entity.setLastname(request.getLastname());
        entity.setBirthDate(request.getBirthDate());
        entity.setDccStatus(this.convertDccStatus(request.getDccStatus()));
        entity.setServiceIdUsed(request.getServiceIdUsed());
        entity.setJti(request.getJti());
        return entity;
    }

    private DccStatusEntity convertDccStatus(final DccStatusRequest request) {
        if (request != null) {
            return DccStatusEntity.builder()
                    .issuer(request.getIssuer())
                    .iat(request.getIat())
                    .sub(request.getSub())
                    .results(this.convertResults(request.getResults()))
                    .confirmation(request.getConfirmation())
                    .build();
        }
        return null;
    }

    private List<DccStatusResultEntity> convertResults(List<ResultRequest> requests) {
        if (requests != null && !requests.isEmpty()) {
            return requests.stream()
                    .map(this::convertResult)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private DccStatusResultEntity convertResult(ResultRequest request) {
        return DccStatusResultEntity.builder()
                .identifier(request.getIdentifier())
                .result(request.getResult())
                .type(request.getType())
                .details(request.getDetails())
                .build();
    }

    private FlightInfoEntity convertFlightInfo(BookingFlightInfoRequest request) {
        if (request != null) {
            return FlightInfoEntity.builder()
                    .from(request.getFrom())
                    .to(request.getTo())
                    .time(request.getTime())
                    .countryOfArrival(request.getCountryOfArrival())
                    .countryOfDeparture(request.getCountryOfDeparture())
                    .regionOfArrival(request.getRegionOfArrival())
                    .regionOfDeparture(request.getRegionOfDeparture())
                    .departureTime(request.getDepartureTime())
                    .arrivalTime(request.getArrivalTime())
                    .type(request.getType())
                    .categories(request.getCategories() != null
                            ? request.getCategories()
                            : new ArrayList<>())
                    .language(request.getLanguage())
                    .conditionTypes(request.getConditionTypes() != null
                            ? request.getConditionTypes()
                            : new ArrayList<>())
                    .build();
        }
        return null;
    }
}
