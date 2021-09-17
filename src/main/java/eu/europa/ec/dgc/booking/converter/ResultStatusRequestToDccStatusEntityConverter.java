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

package eu.europa.ec.dgc.booking.converter;

import eu.europa.ec.dgc.booking.dto.ResultStatusRequest;
import eu.europa.ec.dgc.booking.dto.ResultStatusRequest.ResultStatusDccStatusRequest;
import eu.europa.ec.dgc.booking.dto.ResultStatusRequest.ResultStatusDccStatusResultRequest;
import eu.europa.ec.dgc.booking.entity.DccStatusEntity;
import eu.europa.ec.dgc.booking.entity.DccStatusResultEntity;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
public class ResultStatusRequestToDccStatusEntityConverter implements Converter<ResultStatusRequest, DccStatusEntity> {

    @Override
    public DccStatusEntity convert(final ResultStatusRequest request) {
        final ResultStatusDccStatusRequest dccStatusRequest = request.getDccStatus();
        final List<DccStatusResultEntity> results = dccStatusRequest.getResults().stream()
                .map(this::convertResult)
                .collect(Collectors.toList());

        return DccStatusEntity.builder()
                .issuer(dccStatusRequest.getIssuer())
                .iat(dccStatusRequest.getIat())
                .sub(dccStatusRequest.getSub())
                .results(results)
                .build();
    }

    private DccStatusResultEntity convertResult(final ResultStatusDccStatusResultRequest request) {
        return DccStatusResultEntity.builder()
                .identifier(request.getIdentifier())
                .result(request.getResult())
                .type(request.getType())
                .details(request.getDetails())
                .build();
    }
}
