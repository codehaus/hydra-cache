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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.hydracache.data.hashing.HashFunction;
import org.hydracache.data.hashing.KetamaBasedHashFunction;
import org.hydracache.protocol.data.codec.DefaultProtocolEncoder;
import org.hydracache.protocol.data.marshaller.DataMessageMarshaller;
import org.hydracache.protocol.data.marshaller.DataMessageXmlMarshaller;
import org.hydracache.server.IdentityMarshaller;
import org.hydracache.server.IdentityXmlMarshaller;
import org.hydracache.server.data.versioning.IncrementVersionFactory;
import org.hydracache.server.data.versioning.VersionXmlMarshaller;
import org.hydracache.server.harmony.storage.HarmonyDataBank;
import org.hydracache.server.httpd.HttpConstants;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * @author nzhu
 * 
 */
public class BaseHttpMethodHandlerTest {

    private Mockery context = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    private final HashFunction hashFunction = new KetamaBasedHashFunction();

    private IncrementVersionFactory versionFactoryMarshaller;

    @Before
    public void initialize() {
        versionFactoryMarshaller = new IncrementVersionFactory();
        versionFactoryMarshaller
                .setIdentityMarshaller(new IdentityMarshaller());
    }

    @Test
    public void testEmptyRequestShouldBeRejected() throws HttpException,
            IOException {
        final HttpRequest emptyRequest = context.mock(HttpRequest.class);
        final HttpResponse emptyResponse = context.mock(HttpResponse.class);
        final HttpContext mockHttpContext = context.mock(HttpContext.class);
        BaseHttpMethodHandler handler = createHandler();
        handler.handle(emptyRequest, emptyResponse, mockHttpContext);
        handler.handle(null, emptyResponse, mockHttpContext);
    }

    @Test
    public void ensureCorrectDataKeyExtraction() {
        BaseHttpMethodHandler handler = createHandler();

        String expectedDataKey = "testKey-12";

        String uri = HttpConstants.SLASH + expectedDataKey;

        checkKeyExtrationResult(handler, expectedDataKey, uri);

        uri = HttpConstants.SLASH + expectedDataKey + HttpConstants.SLASH;

        checkKeyExtrationResult(handler, expectedDataKey, uri);

        uri = HttpConstants.SLASH + expectedDataKey + HttpConstants.SLASH + " ";

        checkKeyExtrationResult(handler, expectedDataKey, uri);

        expectedDataKey = "jsession94598429";
        uri = HttpConstants.SLASH + "session/jsession94598429";

        checkKeyExtrationResult(handler, expectedDataKey, uri);
    }

    @Test
    public void ensureCorrectContextExtraction() {
        BaseHttpMethodHandler handler = createHandler();

        String expectedContext = "registry";

        String uri = HttpConstants.SLASH + expectedContext;

        assertEquals("Extracted context is incorrect", expectedContext, handler
                .extractRequestString(uri));
    }

    @Test
    public void ensureContextBasedKeyExtraction() {
        BaseHttpMethodHandler handler = createHandler();

        String expectedDataKey = "shoppingCart";

        String uri = "/context/shoppingCart";

        checkKeyExtrationResult(handler, expectedDataKey, uri);
    }
    
    @Test
    public void ensureNestedContextBasedKeyExtraction() {
        BaseHttpMethodHandler handler = createHandler();

        String expectedDataKey = "shoppingCart/subCart";

        String uri = "/context/shoppingCart/subCart";

        checkKeyExtrationResult(handler, expectedDataKey, uri);
    }
    
    @Test
    public void ensureParameterAfterKeyIsIgnored() {
        BaseHttpMethodHandler handler = createHandler();

        String expectedDataKey = "shoppingCart";

        String uri = "/context/shoppingCart?protocol=xml";

        checkKeyExtrationResult(handler, expectedDataKey, uri);
    }
    
    @Test
    public void ensureKeyExtractionWithEndSlash() {
        BaseHttpMethodHandler handler = createHandler();

        String expectedDataKey = "shoppingCart";

        String uri = "/shoppingCart/";

        checkKeyExtrationResult(handler, expectedDataKey, uri);
    }

    private void checkKeyExtrationResult(BaseHttpMethodHandler handler,
            String expectedDataKey, String uri) {
        assertEquals("Extracted data key is incorrect", expectedDataKey, handler.extractRequestString(uri));
    }
    
    @Test
    public void ensureContextExtractionReturnBlankIfNa() {
        BaseHttpMethodHandler handler = createHandler();

        String expectedContext = "";

        String uri = "/shoppingCart";

        assertEquals("Extracted data context is incorrect", expectedContext, handler.extractRequestContext(uri));
    }
    
    @Test
    public void ensureContextExtraction() {
        BaseHttpMethodHandler handler = createHandler();

        String expectedContext = "context";

        String uri = "/context/shoppingCart";

        assertEquals("Extracted data context is incorrect", expectedContext, handler.extractRequestContext(uri));
    }
    
    @Test
    public void ensureNestedContextExtraction() {
        BaseHttpMethodHandler handler = createHandler();

        String expectedContext = "context";

        String uri = "/context/subContext/shoppingCart";

        assertEquals("Extracted data context is incorrect", expectedContext, handler.extractRequestContext(uri));
    }
    
    @Test
    public void ensureContextExtractionWithEndSlash() {
        BaseHttpMethodHandler handler = createHandler();

        String expectedContext = "";

        String uri = "/shoppingCart#1/";

        assertEquals("Extracted data context is incorrect", expectedContext, handler.extractRequestContext(uri));
    }

    private BaseHttpMethodHandler createHandler() {
        HarmonyDataBank dataBank = context.mock(HarmonyDataBank.class);

        BaseHttpMethodHandler handler = new StubHandler(dataBank,
                versionFactoryMarshaller);

        return handler;
    }

    private class StubHandler extends BaseHttpMethodHandler {

        public StubHandler(HarmonyDataBank dataBank,
                IncrementVersionFactory versionFactoryMarshaller) {
            super(dataBank, BaseHttpMethodHandlerTest.this.hashFunction,
                    new DefaultProtocolEncoder(new DataMessageMarshaller(
                            versionFactoryMarshaller),
                            new DataMessageXmlMarshaller(new VersionXmlMarshaller(
                                    new IdentityXmlMarshaller(), versionFactoryMarshaller))));
        }

        @Override
        public void handle(HttpRequest request, HttpResponse response,
                HttpContext context) throws HttpException, IOException {
        }

    }

}
