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

import eu.europa.ec.dgc.booking.dto.BoardingPassDto;
import eu.europa.ec.dgc.booking.dto.FlightInfoDto;
import eu.europa.ec.dgc.booking.entity.BookingEntity;
import eu.europa.ec.dgc.booking.entity.FlightInfoEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
public class BookingEntityToBoardingPassDtoConverter implements Converter<BookingEntity, BoardingPassDto> {

    @Override
    public BoardingPassDto convert(final BookingEntity entity) {
        final BoardingPassDto dto = new BoardingPassDto();
        dto.setReference(entity.getReference());
        dto.setFlightInfo(convertFlightInfo(entity.getFlightInfo()));
        if (!entity.getPassengers().isEmpty() && entity.getPassengers().get(0).getDccStatus() != null) {
            dto.setConfirmations(entity.getPassengers().get(0).getDccStatus().getConfirmation());
        }
        return dto;
    }

    private FlightInfoDto convertFlightInfo(final FlightInfoEntity entity) {
        final FlightInfoDto dto = new FlightInfoDto();
        dto.setFrom(entity.getFrom());
        dto.setTo(entity.getTo());
        dto.setTime(entity.getTime());
        dto.setCountryOfArrival(entity.getCountryOfArrival());
        dto.setCountryOfDeparture(entity.getCountryOfDeparture());
        dto.setRegionOfArrival(entity.getRegionOfArrival());
        dto.setRegionOfDeparture(entity.getRegionOfDeparture());
        dto.setDepartureTime(entity.getDepartureTime());
        dto.setArrivalTime(entity.getArrivalTime());
        return dto;
    }
}
