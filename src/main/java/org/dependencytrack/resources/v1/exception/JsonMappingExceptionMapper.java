/*
 * This file is part of Dependency-Track.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright (c) Steve Springett. All Rights Reserved.
 */
package org.dependencytrack.resources.v1.exception;

import com.fasterxml.jackson.core.exc.StreamConstraintsException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.dependencytrack.resources.v1.problems.ProblemDetails;
import org.dependencytrack.resources.v1.vo.BomSubmitRequest;
import org.dependencytrack.resources.v1.vo.VexSubmitRequest;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Objects;

/**
 * @since 4.11.0
 */
@Provider
@Priority(1)
public class JsonMappingExceptionMapper implements ExceptionMapper<JsonMappingException> {

    @Context
    private HttpServletRequest request;

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public Response toResponse(final JsonMappingException exception) {
        final var problemDetails = new ProblemDetails();
        problemDetails.setStatus(400);
        problemDetails.setTitle("The provided JSON payload could not be mapped");
        problemDetails.setDetail(createDetail(exception));

        return Response
                .status(Response.Status.BAD_REQUEST)
                .type(ProblemDetails.MEDIA_TYPE_JSON)
                .entity(problemDetails)
                .build();
    }

    private static String createDetail(final JsonMappingException exception) {
        if (!(exception.getCause() instanceof StreamConstraintsException)) {
            return exception.getMessage();
        }

        final JsonMappingException.Reference reference = exception.getPath().get(0);
        if (Objects.equals(reference.getFrom(), BomSubmitRequest.class)
            && "bom".equals(reference.getFieldName())) {
            return """
                    The BOM is too large to be transmitted safely via Base64 encoded JSON value. \
                    Please use the "POST /api/v1/bom" endpoint with Content-Type "multipart/form-data" instead. \
                    Original cause: %s""".formatted(exception.getMessage());
        } else if (Objects.equals(reference.getFrom(), VexSubmitRequest.class)
                   && "vex".equals(reference.getFieldName())) {
            return """
                    The VEX is too large to be transmitted safely via Base64 encoded JSON value. \
                    Please use the "POST /api/v1/vex" endpoint with Content-Type "multipart/form-data" instead. \
                    Original cause: %s""".formatted(exception.getMessage());
        }

        return exception.getMessage();
    }

}
