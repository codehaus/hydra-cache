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
import org.hydracache.server.harmony.storage.HarmonyDataBank;
import org.hydracache.server.httpd.HttpConstants;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
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
        
        expectedDataKey = "session/jsession94598429";
        uri = HttpConstants.SLASH + expectedDataKey + " ";

        checkKeyExtrationResult(handler, expectedDataKey, uri);
    }

    @Test
    public void ensureCorrectContextExtraction() {
        BaseHttpMethodHandler handler = createHandler();

        String expectedContext = "registry";

        String uri = HttpConstants.SLASH + expectedContext;
        
        assertEquals("Extracted context is incorrect", expectedContext,
                handler.extractRequestString(uri));
    }

    @Test
    public void ensureNamingSpaceIsSupported() {
        BaseHttpMethodHandler handler = createHandler();

        String expectedDataKey = "session/jsession?487823";

        String uri = HttpConstants.SLASH + expectedDataKey;

        checkKeyExtrationResult(handler, expectedDataKey, uri);
    }

    private BaseHttpMethodHandler createHandler() {
        HarmonyDataBank dataBank = context.mock(HarmonyDataBank.class);

        BaseHttpMethodHandler handler = new StubHandler(dataBank);

        return handler;
    }

    private void checkKeyExtrationResult(BaseHttpMethodHandler handler,
            String expectedDataKey, String uri) {
        assertEquals("Extracted data key is incorrect", hashFunction.hash(expectedDataKey),
                handler.extractDataKeyHash(uri).longValue());
    }

    private class StubHandler extends BaseHttpMethodHandler {

        public StubHandler(HarmonyDataBank dataBank) {
            super(dataBank, BaseHttpMethodHandlerTest.this.hashFunction);
        }

        @Override
        public void handle(HttpRequest request, HttpResponse response,
                HttpContext context) throws HttpException, IOException {
        }

    }

}
