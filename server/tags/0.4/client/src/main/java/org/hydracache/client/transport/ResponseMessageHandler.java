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

/**
 * Encapsulates the logic needed to deal with the response handling from
 * transport requests.
 * 
 * @author Tan Quach
 * @since 1.0
 */
public interface ResponseMessageHandler {
    /**
     * Performs any actions based on the the response body and response code.
     * 
     * @param responseCode An HTTP response code
     * @param responseBody The data contained in the response
     * @return A custom message data structure properly constructed 
     * @throws Exception If there is a problem with the response.
     */
    public ResponseMessage accept(int responseCode, byte[] responseBody) throws Exception;
}
