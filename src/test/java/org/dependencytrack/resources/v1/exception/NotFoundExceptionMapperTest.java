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

import org.junit.Test;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class NotFoundExceptionMapperTest {

    @Test
    @SuppressWarnings("resource")
    public void testToResponse() {
        final Response response = new NotFoundExceptionMapper().toResponse(new NotFoundException());
        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(response.getEntity()).isNull();
    }

}