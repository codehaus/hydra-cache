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
 * @author Tan Quach
 * @since 1.0
 */
public class NullTransport implements Transport {

    private ResponseMessage responseMessage;

    /**
     * @param responseMessage
     */
    public NullTransport() {
        super();
    }
    
    public void setResponseMessage(ResponseMessage msg) {
        this.responseMessage = msg;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.client.transport.Transport#cleanUpConnection()
     */
    @Override
    public void cleanUpConnection() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hydracache.client.transport.Transport#establishConnection(java.lang
     * .String, int)
     */
    @Override
    public Transport establishConnection(String hostName, int port) {
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hydracache.client.transport.Transport#registerHandler(java.lang.Integer
     * , org.hydracache.client.transport.ResponseMessageHandler)
     */
    @Override
    public void registerHandler(Integer statusCode, ResponseMessageHandler handler) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hydracache.client.transport.Transport#sendRequest(org.hydracache.
     * client.transport.RequestMessage)
     */
    @Override
    public ResponseMessage sendRequest(RequestMessage requestMessage) throws Exception {
        return responseMessage;
    }

}