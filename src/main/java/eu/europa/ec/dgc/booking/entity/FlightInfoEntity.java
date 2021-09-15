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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.javafaker.Address;
import com.github.javafaker.Faker;
import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Builder
@Getter
@ToString
@EqualsAndHashCode
@Jacksonized
public class FlightInfoEntity {

    @JsonProperty("from")
    private String from;

    @JsonProperty("to")
    private String to;

    @JsonProperty("time")
    private OffsetDateTime time;
    
    @JsonProperty("coa")
    private String countryOfArrival;
    
    @JsonProperty("cod")
    private String countryOfDeparture;
    
    // Region of Arrival ISO 3166-2 without Country
    @JsonProperty("roa")
    private String regionOfArrival;
    
    // Region of Departure ISO 3166-2 without Country
    @JsonProperty("rod")
    private String regionOfDeparture;
        
    private OffsetDateTime departureTime;
    
    private OffsetDateTime arrivalTime;

    /**
     * Create a random FlightInfoEntity that is 2 days in the future.
     * 
     * @return {@link FlightInfoEntity}
     */
    public static FlightInfoEntity random() {
        final Address departure = new Faker().address();
        final String countryOfDeparture = departure.countryCode();
        final String from = departure.cityName();
        
        final Address arrival = new Faker().address();
        final String countryOfArrival = arrival.countryCode();
        final String to = arrival.cityName();
        
        final OffsetDateTime now = OffsetDateTime.now();
        final OffsetDateTime departureTime = now.plusDays(1);
        return FlightInfoEntity.builder()
                .from(from)
                .countryOfDeparture(countryOfDeparture)
                .regionOfDeparture(countryOfDeparture)
                .to(to)
                .countryOfArrival(countryOfArrival)
                .regionOfArrival(countryOfArrival)
                .departureTime(departureTime)
                .arrivalTime(departureTime.plusHours(8).plusMinutes(24))
                .time(departureTime)
                .build();
    }
}
