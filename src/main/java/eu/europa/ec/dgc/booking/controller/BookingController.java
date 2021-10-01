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

package eu.europa.ec.dgc.booking.controller;

import eu.europa.ec.dgc.booking.dto.BookingReplaceRequest;
import eu.europa.ec.dgc.booking.dto.BookingRequest;
import eu.europa.ec.dgc.booking.dto.BookingResponse;
import eu.europa.ec.dgc.booking.dto.DevDccStatus;
import eu.europa.ec.dgc.booking.entity.BookingEntity;
import eu.europa.ec.dgc.booking.exception.BookingNotFoundException;
import eu.europa.ec.dgc.booking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BookingController {

    private static final String PATH = "/booking";

    private static final String PATH_REPLACE = "/booking/replace";

    private final BookingService bookingService;

    private final ConversionService converter;

    /**
     * Create new Booking in Session.
     * 
     * @param booking Data that will be created
     * @param dccStatus Dev mode, changing status.
     * @return current Booking object
     */
    @Operation(summary = "Booking Route", description = "Booking Route")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request / Validation errors"),
        @ApiResponse(responseCode = "404", description = "Not Found"),
        @ApiResponse(responseCode = "415", description = "Unsupported Media Type"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error"),
        @ApiResponse(responseCode = "501", description = "Not Implemented")
    })
    @ResponseStatus(code = HttpStatus.OK)
    @PostMapping(path = PATH, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BookingResponse booking(
            @Valid @RequestBody final BookingRequest booking,
            @RequestParam(name = "setDccStatus", required = false) final DevDccStatus dccStatus,
            HttpSession session) {
        final String sessionId = session.getId();
        log.debug("Incoming POST request to '{}' with content '{}', optional dccStatus '{}' and sessionId '{}'",
                PATH, booking, dccStatus, sessionId);
        bookingService.create(sessionId, booking, dccStatus);

        try {
            return converter.convert(bookingService
                    .getBySessionId(sessionId), BookingResponse.class);
        } catch (BookingNotFoundException e) {
            return converter.convert(bookingService
                    .getByReference(booking.getBookingReference()), BookingResponse.class);
        }
    }

    /**
     * Replace Booking Data.
     * 
     * @param request {@link BookingReplaceRequest}
     * @param session {@link HttpSession}
     * @return {@link BookingResponse}
     */
    @Operation(summary = "Replace Booking Data", description = "Replace Booking Data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request / Validation errors"),
        @ApiResponse(responseCode = "415", description = "Unsupported Media Type"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @ResponseStatus(code = HttpStatus.OK)
    @PostMapping(path = PATH_REPLACE, 
        consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BookingResponse replace(
            @Valid @RequestBody final BookingReplaceRequest request, final HttpSession session) {
        final String sessionId = session.getId();
        log.debug("Incoming POST request to '{}' with content '{}' and sessionId '{}'",
                PATH_REPLACE, request, sessionId);
        final BookingEntity entity = bookingService.replace(sessionId, request);
        return converter.convert(entity, BookingResponse.class);
    }
}
