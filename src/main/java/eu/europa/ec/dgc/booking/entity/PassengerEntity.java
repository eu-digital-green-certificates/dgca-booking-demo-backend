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

import com.github.javafaker.Faker;
import eu.europa.ec.dgc.booking.dto.BookingRequest;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class PassengerEntity {

    private final UUID id;

    @Setter
    private String forename;

    @Setter
    private String lastname;

    @Setter
    private DccStatusEntity dccStatus;

    public PassengerEntity() {
        this.id = UUID.randomUUID();
    }

    /**
     * Build PassengerEntity from frontend object {@link BookingRequest}.
     * 
     * @param bookingRequest {@link PassengerEntity}
     * @return {@link PassengerEntity}
     */
    public static PassengerEntity build(BookingRequest bookingRequest) {
        PassengerEntity passengerData = new PassengerEntity();
        passengerData.setForename(bookingRequest.getForename());
        passengerData.setLastname(bookingRequest.getLastname());
        return passengerData;
    }

    /**
     * Build a PassengerEntity with random first and lastname.
     * 
     * @return {@link PassengerEntity}
     */
    public static PassengerEntity random() {
        Faker faker = new Faker();

        PassengerEntity passengerData = new PassengerEntity();
        passengerData.setForename(faker.name().firstName());
        passengerData.setLastname(faker.name().lastName());
        return passengerData;
    }
}
