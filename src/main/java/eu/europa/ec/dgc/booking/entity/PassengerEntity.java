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
import java.time.LocalDate;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class PassengerEntity {

    private UUID id;

    @Setter
    private String forename;

    @Setter
    private String lastname;

    @Setter
    private LocalDate birthDate;

    @Setter
    private DccStatusEntity dccStatus;
    
    // service id that was used for the token
    @Setter
    private String serviceIdUsed;

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
        if (bookingRequest.getId() != null) {
            passengerData.id = bookingRequest.getId();
        }
        passengerData.setForename(bookingRequest.getForename());
        passengerData.setLastname(bookingRequest.getLastname());
        passengerData.setBirthDate(bookingRequest.getBirthDate());
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

    /**
     * Always creates the same passenger based on the pos. Pos 0-2 are possible.
     * 
     * @param pos supports 0-2
     * @return {@link PassengerEntity}
     */
    public static PassengerEntity immutable(int pos) {
        PassengerEntity entity = new PassengerEntity();
        if (pos == 0) {
            entity.id = UUID.fromString("6751B6A6-A31D-44DA-9C0F-ECCCF4F19338");
            entity.setForename("Lionel");
            entity.setLastname("Kuhic");
            entity.setBirthDate(LocalDate.of(1994, 5, 25));
        } else if (pos == 1) {
            entity.id = UUID.fromString("B67F6578-08D9-4254-BCB8-4936053865C6");
            entity.setForename("Fidel");
            entity.setLastname("Lang");
            entity.setBirthDate(LocalDate.of(1978, 8, 14));
        } else if (pos == 2) {
            entity.id = UUID.fromString("CB992C09-48EC-4C5B-9303-C2DC06E7496D");
            entity.setForename("Demetria");
            entity.setLastname("Hagenes");
            entity.setBirthDate(LocalDate.of(2002, 4, 25));
        } else {
            throw new IllegalArgumentException(String.format("Unsupported pos '%s'", pos));
        }
        return entity;
    }
}
