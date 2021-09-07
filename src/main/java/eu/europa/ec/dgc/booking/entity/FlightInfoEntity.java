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

    /**
     * Create a random FlightInfoEntity that is 2 days in the future.
     * 
     * @return {@link FlightInfoEntity}
     */
    public static FlightInfoEntity random() {
        Faker faker = new Faker();

        return FlightInfoEntity.builder()
                .from(faker.address().cityName())
                .to(faker.address().cityName())
                .time(OffsetDateTime.now().plusDays(2))
                .build();
    }
}
