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
package org.hydracache.server.httpd;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.*;

import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.junit.Test;

/**
 * @author nzhu
 * 
 */
public class HttpParamsFactoryTest {

    @Test
    public void testSimpleCreation() {
        HttpParamsFactory factory = new HttpParamsFactory();

        HttpParams params = factory.create();

        assertNotNull(params);
    }

    @Test
    public void testSocketBufferSizeSetting() {
        HttpParamsFactory factory = new HttpParamsFactory();

        int expectedBufferSize = 1024;

        factory.setSocketBufferSize(expectedBufferSize);

        HttpParams params = factory.create();

        assertEquals("Socket buffer size is not correctly set",
                expectedBufferSize, params.getIntParameter(
                        CoreConnectionPNames.SOCKET_BUFFER_SIZE, 0));
    }

    @Test
    public void testSocketTimeoutSetting() {
        HttpParamsFactory factory = new HttpParamsFactory();

        int expectedSocketTimeout = 500;

        factory.setSocketTimeout(expectedSocketTimeout);

        HttpParams params = factory.create();

        assertEquals("Socket timeout is not correctly set",
                expectedSocketTimeout, params.getIntParameter(
                        CoreConnectionPNames.SO_TIMEOUT, 0));
    }

}
