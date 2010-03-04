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

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * @author nzhu
 * 
 */
public class MethodBasedRequestDispatcherTest {
    private Mockery context = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    private final HttpRequest request = context.mock(HttpRequest.class);

    private final HttpResponse response = context.mock(HttpResponse.class);

    private final HttpContext httpContext = context.mock(HttpContext.class);

    private final HttpGetMethodHandler getHandler = context
            .mock(HttpGetMethodHandler.class);

    private final HttpPutMethodHandler putHandler = context
            .mock(HttpPutMethodHandler.class);

    private final HttpDeleteMethodHandler deleteHandler = context
            .mock(HttpDeleteMethodHandler.class);

    private final UnsupportedHttpMethodHandler unsupportedHttpMethodHandler = context
            .mock(UnsupportedHttpMethodHandler.class);

    @Test
    public void ensureErrorHandlingWithNullMessage() throws Exception {
        stubGetHandleFailureWithNullErrMsg();

        addGenerateErrorResponseExp();

        MethodBasedRequestDispatcher dispatcher = createDispatcher();

        dispatcher.dispatch(request, response, httpContext, "GET");

        context.assertIsSatisfied();
    }

    private void stubGetHandleFailureWithNullErrMsg() throws HttpException,
            IOException {
        context.checking(new Expectations() {
            {
                one(getHandler).handle(request, response, httpContext);
                will(throwException(new RuntimeException()));
            }
        });
    }

    @Test
    public void ensureErrorHandlingIsCorrect() throws Exception {
        addGetHandleFailureExp();

        addGenerateErrorResponseExp();

        MethodBasedRequestDispatcher dispatcher = createDispatcher();

        dispatcher.dispatch(request, response, httpContext, "GET");

        context.assertIsSatisfied();
    }

    private void addGetHandleFailureExp() throws HttpException, IOException {
        context.checking(new Expectations() {
            {
                one(getHandler).handle(request, response, httpContext);
                will(throwException(new RuntimeException("Something wrong")));
            }
        });
    }

    private void addGenerateErrorResponseExp() {
        context.checking(new Expectations() {
            {
                one(response)
                        .setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                one(response).setEntity(with(any(StringEntity.class)));
            }
        });
    }

    @Test
    public void testDispatchPutMethod() throws HttpException, IOException {
        addSuccessHandlePutExp();

        MethodBasedRequestDispatcher dispatcher = createDispatcher();

        dispatcher.dispatch(request, response, httpContext, "PUT");

        context.assertIsSatisfied();
    }

    private void addSuccessHandlePutExp() throws HttpException, IOException {
        context.checking(new Expectations() {
            {
                one(putHandler).handle(request, response, httpContext);
            }
        });
    }

    @Test
    public void testDispatchGetMethod() throws HttpException, IOException {
        addSuccessHandleGetExp();

        MethodBasedRequestDispatcher dispatcher = createDispatcher();

        dispatcher.dispatch(request, response, httpContext, "GET");

        context.assertIsSatisfied();
    }

    private void addSuccessHandleGetExp() throws HttpException, IOException {
        context.checking(new Expectations() {
            {
                one(getHandler).handle(request, response, httpContext);
            }
        });
    }

    @Test
    public void testDispatchPostMethod() throws HttpException, IOException {
        addSuccessHandleUnknownExp();

        MethodBasedRequestDispatcher dispatcher = createDispatcher();

        dispatcher.dispatch(request, response, httpContext, "POST");

        context.assertIsSatisfied();
    }

    private void addSuccessHandleUnknownExp() throws HttpException, IOException {
        context.checking(new Expectations() {
            {
                one(unsupportedHttpMethodHandler).handle(request, response,
                        httpContext);
            }
        });
    }

    private MethodBasedRequestDispatcher createDispatcher() {
        MethodBasedRequestDispatcher dispatcher = new MethodBasedRequestDispatcher(
                getHandler, putHandler, deleteHandler,
                unsupportedHttpMethodHandler);

        return dispatcher;
    }
}
