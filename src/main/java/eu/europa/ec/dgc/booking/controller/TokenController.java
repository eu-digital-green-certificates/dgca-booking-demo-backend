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

import eu.europa.ec.dgc.booking.dto.BookingResponse;
import eu.europa.ec.dgc.booking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TokenController {

    private static final String PATH = "/tokencontent/{subject}";

    private final BookingService bookingService;

    private final ConversionService converter;

    @Operation(summary = "Token Route (private)", description = "Token Route (private)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request / Validation errors"),
        @ApiResponse(responseCode = "404", description = "Not Found"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error"),
        @ApiResponse(responseCode = "501", description = "Not Implemented")
    })
    @ResponseStatus(code = HttpStatus.OK)
    @GetMapping(path = PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public BookingResponse tokencontent(
            @PathVariable(value = "subject", required = true) final String passengerId,
            @RequestParam(value = "service", required = false) final String serviceId) {
        log.debug("Incoming GET request to '{}' with passenger ID '{}' and service ID '{}'", PATH, passengerId, serviceId);
        return converter.convert(bookingService.getOnlyPassengerId(passengerId, serviceId), BookingResponse.class);
    }
}
