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

import eu.europa.ec.dgc.booking.dto.ResultStatusRequest;
import eu.europa.ec.dgc.booking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ResultController {

    private static final String PATH = "/result/{subject}";


    private final BookingService bookingService;

    /**
     * Receives the DCC Status of an subject from the validation decorator and update the passenger.
     * 
     * @param passengerId Subject
     * @param result      {@link ResultStatusRequest}
     */
    @Operation(summary = "Result Route (private)", description = "Result Route (private)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request / Validation errors"),
        @ApiResponse(responseCode = "404", description = "Not Found"),
        @ApiResponse(responseCode = "415", description = "Unsupported Media Type"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error"),
        @ApiResponse(responseCode = "501", description = "Not Implemented")
    })
    @ResponseStatus(code = HttpStatus.OK)
    @PutMapping(path = PATH, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void resultSubject(
            @PathVariable(value = "subject", required = true) final String passengerId,
            @Valid @RequestBody final ResultStatusRequest result) {
        log.debug("Incoming PUT request to '{}' with passenger ID '{}' and content '{}'", PATH, passengerId, result);
        final int updateCount = bookingService.updateResult(passengerId, result);
        log.debug("Update '{}' passengers", updateCount);
    }
}
