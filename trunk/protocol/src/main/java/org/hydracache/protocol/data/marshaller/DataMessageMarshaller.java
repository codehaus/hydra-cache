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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.commons.lang.Validate;
import org.hydracache.io.IoUtils;
import org.hydracache.io.Marshaller;
import org.hydracache.protocol.data.message.BlobDataMessage;
import org.hydracache.server.data.versioning.Version;

/**
 * {@link Marshaller} used to handle {@link DataMessage} serialization
 * 
 * @author nzhu
 * 
 */
public class DataMessageMarshaller implements Marshaller<BlobDataMessage> {

    private Marshaller<Version> versionMarshaller;

    /**
     * Constructor
     */
    public DataMessageMarshaller(Marshaller<Version> versionMarshaller) {
        this.versionMarshaller = versionMarshaller;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.io.Marshaller#readObject(java.io.DataInputStream)
     */
    @Override
    public BlobDataMessage readObject(DataInputStream dataIn)
            throws IOException {
        BlobDataMessage msg = new BlobDataMessage();

        msg.setVersion(versionMarshaller.readObject(dataIn));

        byte[] bytes = IoUtils.readRemainingBytes(dataIn);

        msg.setBlob(bytes);

        return msg;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.io.Marshaller#writeObject(java.lang.Object,
     * java.io.DataOutputStream)
     */
    @Override
    public void writeObject(BlobDataMessage msg, DataOutputStream dataOut)
            throws IOException {
        Validate.notNull(msg.getBlob(), "Data message is empty");

        versionMarshaller.writeObject(msg.getVersion(), dataOut);

        dataOut.write(msg.getBlob());
    }

}
