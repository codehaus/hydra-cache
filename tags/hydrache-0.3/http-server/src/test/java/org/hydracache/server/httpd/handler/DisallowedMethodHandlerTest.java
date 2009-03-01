/*
 * Copyright 2008 the original author or authors.
 *
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
 */
package org.hydracache.server.httpd.handler;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.protocol.HttpContext;
import org.hydracache.server.httpd.handler.UnsupportedHttpMethodHandler;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

/**
 * @author nzhu
 * 
 */
public class DisallowedMethodHandlerTest {
    private Mockery context = new Mockery();

    /**
     * Test method for
     * {@link org.hydracache.server.httpd.handler.UnsupportedHttpMethodHandler#handle(org.apache.http.HttpRequest, org.apache.http.HttpResponse, org.apache.http.protocol.HttpContext)}
     * .
     * 
     * @throws Exception
     */
    @Test
    public void testHandlerWillGenerateProperResponseCode() throws Exception {
        UnsupportedHttpMethodHandler handler = new UnsupportedHttpMethodHandler();

        HttpResponse response = context.mock(HttpResponse.class);

        {
            addSetCorrectStatusCodeExpectation(response);

            addHtmlContentIsGeneratedExpectation(response);
        }

        handler.handle(context.mock(HttpRequest.class), response, context
                .mock(HttpContext.class));

        context.assertIsSatisfied();
    }

    private void addHtmlContentIsGeneratedExpectation(
            final HttpResponse response) {
        context.checking(new Expectations() {
            {
                one(response).setEntity(with(any(NStringEntity.class)));
            }
        });
    }

    private void addSetCorrectStatusCodeExpectation(final HttpResponse response) {
        context.checking(new Expectations() {
            {
                one(response).setStatusCode(HttpStatus.SC_METHOD_NOT_ALLOWED);
            }
        });
    }

}
