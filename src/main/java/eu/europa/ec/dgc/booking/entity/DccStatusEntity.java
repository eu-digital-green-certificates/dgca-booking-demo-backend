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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class DccStatusEntity {

    // Issuer URL
    private String issuer;

    // Seconds since epoch
    private long iat;

    // Value of the access token
    private String sub;

    @Builder.Default
    private List<DccStatusResultEntity> results = new ArrayList<>();

    private String confirmation;

    /**
     * Build DCCStatusEntity Object with EMPTY result.
     * 
     * @return {@link DccStatusEntity}
     */
    public static DccStatusEntity empty() {
        return demoBuilder().build();
    }

    /**
     * Build DCCStatusEntity Object with OPEN result.
     * 
     * @return {@link DccStatusEntity}
     */
    public static DccStatusEntity open() {
        return demoBuilder().results(Arrays.asList(DccStatusResultEntity.open())).build();
    }

    /**
     * Build DCCStatusEntity Object with FAILED result.
     * 
     * @return {@link DccStatusEntity}
     */
    public static DccStatusEntity failed() {
        return demoBuilder().results(Arrays.asList(DccStatusResultEntity.failed())).build();
    }

    /**
     * Build DCCStatusEntity Object with PASSED result.
     * 
     * @return {@link DccStatusEntity}
     */
    public static DccStatusEntity passed() {
        return demoBuilder().results(Arrays.asList(DccStatusResultEntity.passed())).build();
    }

    private static DccStatusEntityBuilder demoBuilder() {
        return DccStatusEntity.builder().issuer("Demo issuer dgca-booking-demo-backend")
                .iat(System.currentTimeMillis() / 1000L).sub("Demo sub").confirmation("Demo confirmation");
    }
}
