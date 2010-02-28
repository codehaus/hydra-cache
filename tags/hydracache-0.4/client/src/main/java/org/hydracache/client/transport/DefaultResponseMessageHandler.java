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
package org.hydracache.client.transport;

import org.apache.commons.httpclient.HttpStatus;

/**
 * @author Tan Quach
 * @since 1.0
 */
public class DefaultResponseMessageHandler implements ResponseMessageHandler {

    /* (non-Javadoc)
     * @see org.hydracache.client.transport.ResponseMessageHandler#accept(int, java.io.InputStream)
     */
    @Override
    public ResponseMessage accept(int responseCode, byte[] responseBody) throws Exception {
        ResponseMessage responseMessage = null;
        if (responseCode == HttpStatus.SC_OK ||
                responseCode == HttpStatus.SC_CREATED)
        {
            responseMessage = new ResponseMessage(true);
            responseMessage.setResponseBody(responseBody);
        }
        return responseMessage;
    }

}
