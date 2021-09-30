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

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BookingReplaceRequest {

    @NotBlank
    private String reference;

    private OffsetDateTime time;

    @Valid
    private List<PassengerRequest> passengers = new ArrayList<>();

    @Valid
    private BookingFlightInfoRequest flightInfo;

    @Data
    public static final class PassengerRequest {

        private UUID id;

        @NotBlank
        private String forename;

        @NotBlank
        private String lastname;

        private String birthDate;

        @Valid
        private DccStatusRequest dccStatus;

        private String serviceIdUsed;

        // AccessTokenPayload.jti
        private String jti;
    }

    @Data
    public static final class DccStatusRequest {

        private String issuer;

        private long iat;

        private String sub;

        @Valid
        private List<ResultRequest> results;

        private String confirmation;
    }

    @Data
    public static final class ResultRequest {

        private String identifier;

        private String result;

        private String type;

        private String details;
    }

    @Data
    public static final class BookingFlightInfoRequest {

        private String from;

        private String to;

        private OffsetDateTime time;

        @JsonProperty("coa")
        private String countryOfArrival;

        @JsonProperty("cod")
        private String countryOfDeparture;

        @JsonProperty("roa")
        private String regionOfArrival;

        @JsonProperty("rod")
        private String regionOfDeparture;

        @JsonProperty("departureTime")
        private OffsetDateTime departureTime;

        @JsonProperty("arrivalTime")
        private OffsetDateTime arrivalTime;

        private int type;

        private List<String> categories;

        @JsonProperty("lang")
        private String language;

        private List<String> conditionTypes;
    }
}
