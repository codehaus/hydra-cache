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
package org.hydracache.protocol.data.marshaller;

import org.apache.commons.lang.Validate;
import org.hydracache.io.Marshaller;
import org.hydracache.protocol.data.message.BlobDataMessage;
import org.hydracache.server.data.versioning.Version;

/**
 * Factory bean used to generate specific message {@link Marshaller}
 * 
 * @author nzhu
 * 
 */
public class MessageMarshallerFactory {

    private Marshaller<Version> versionMarshaller;

    /**
     * Constructor
     */
    public MessageMarshallerFactory(Marshaller<Version> versionMarshaller) {
        this.versionMarshaller = versionMarshaller;
    }

    /**
     * Create or retrieve {@link Marshaller} implementation for the given
     * message type
     * 
     * @return marshaller instance
     */
    public Marshaller<BlobDataMessage> createMarshallerFor(
            int messageType) {
        Marshaller<BlobDataMessage> marshaller = null;

        switch (messageType) {
        case BlobDataMessage.BLOB_DATA_MESSAGE_TYPE:
            marshaller = new DataMessageMarshaller(versionMarshaller);
            break;
        }

        Validate.notNull(marshaller, "Unknown message type[" + messageType
                + "] failed to create message marshaller");

        return marshaller;
    }

}
