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

import java.time.OffsetDateTime;
import com.github.javafaker.Faker;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class FlightInfoEntity {

    private String from;
    
    private String to;
    
    private OffsetDateTime time;
    
    public static FlightInfoEntity random() {
        Faker faker = new Faker();
        
        return FlightInfoEntity.builder()
                .from(faker.address().cityName())
                .to(faker.address().cityName())
                .time(OffsetDateTime.now().plusDays(2))
                .build();
    }
}
