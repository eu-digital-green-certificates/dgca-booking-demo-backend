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
package eu.europa.ec.dgc.booking.repository;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import eu.europa.ec.dgc.booking.entity.BookingEntity;
import eu.europa.ec.dgc.booking.exception.BookingNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingRepository {

    private static final String BOOKING_OBJECT_KEY = "booking";
    
    private final HttpSession httpSession;
    
    public void clean() {
        httpSession.setAttribute(BOOKING_OBJECT_KEY, null);
    }

    public void save(BookingEntity bookingEntity) {
        httpSession.setAttribute(BOOKING_OBJECT_KEY, bookingEntity);
    }

    public BookingEntity get() {
        Object attr = httpSession.getAttribute(BOOKING_OBJECT_KEY);
        if(attr instanceof BookingEntity) {
            return (BookingEntity) attr;
        }
        throw new BookingNotFoundException();
    }
}
