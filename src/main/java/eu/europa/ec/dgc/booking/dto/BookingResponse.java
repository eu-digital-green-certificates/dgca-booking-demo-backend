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

package eu.europa.ec.dgc.booking.dto;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class BookingResponse {

    private String reference;

    private OffsetDateTime time;

    private List<BookingPassengerResponse> passengers = new ArrayList<>();

    private BookingFlightInfoResponse flightInfo;

    @Data
    public static final class BookingPassengerResponse {

        private UUID id;

        private String forename;

        private String lastname;

        private BookingPassengerDccStatusResponse dccStatus;
    }

    @Data
    public static final class BookingPassengerDccStatusResponse {

        private String issuer;

        private long iat;

        private String sub;

        private List<BookingPassengerDccStatusResultResponse> results;

        private String confirmation;
    }

    @Data
    public static final class BookingPassengerDccStatusResultResponse {

        private String identifier;

        private String result;

        private String type;

        private String details;
    }

    @Data
    public static final class BookingFlightInfoResponse {

        private String from;

        private String to;

        private OffsetDateTime time;
    }
}
