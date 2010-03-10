/*
 * Copyright 2010 the original author or authors.
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

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import org.hydracache.data.hashing.HashFunction;
import org.hydracache.data.hashing.KetamaBasedHashFunction;
import org.hydracache.protocol.data.codec.DefaultProtocolEncoder;
import org.hydracache.protocol.data.marshaller.DataMessageMarshaller;
import org.hydracache.protocol.data.marshaller.DataMessageXmlMarshaller;
import org.hydracache.server.IdentityMarshaller;
import org.hydracache.server.IdentityXmlMarshaller;
import org.hydracache.server.data.versioning.IncrementVersionFactory;
import org.hydracache.server.data.versioning.VersionXmlMarshaller;
import org.junit.Test;

/**
 * @author Nick Zhu (nzhu@jointsource.com)
 * 
 */
public class HttpDeleteMethodHandlerTest extends AbstractHttpMethodHandlerTest {

    @Test
    public void ensureHandleGetDataCorrectly() throws HttpException,
            IOException {
        stubGetRequestURI(mockRequest, "/testContext/testKey");

        BaseHttpMethodHandler handler = createHttpGetMethodHandler();

        handler.handle(mockRequest, mockResponse, mockHttpContext);

        verify(mockDataBank).delete(eq("testContext"), anyLong());
        verify(mockResponse).setStatusCode(HttpStatus.SC_OK);
    }

    private HttpDeleteMethodHandler createHttpGetMethodHandler() {
        HashFunction hashFunction = new KetamaBasedHashFunction();

        IncrementVersionFactory versionMarshaller = new IncrementVersionFactory(
                new IdentityMarshaller());

        DefaultProtocolEncoder messageEncoder = new DefaultProtocolEncoder(
                new DataMessageMarshaller(versionMarshaller),
                new DataMessageXmlMarshaller(new VersionXmlMarshaller(
                        new IdentityXmlMarshaller(), versionMarshaller)));

        HttpDeleteMethodHandler handler = new HttpDeleteMethodHandler(
                mockDataBank, hashFunction, messageEncoder);

        return handler;
    }

}
