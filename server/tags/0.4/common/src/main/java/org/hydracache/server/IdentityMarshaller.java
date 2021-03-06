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
package org.hydracache.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;

import org.hydracache.io.BinaryMarshaller;

/**
 * @author nzhu
 * 
 */
public class IdentityMarshaller implements BinaryMarshaller<Identity> {

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.io.BinaryMarshaller#readObject(java.io.DataInputStream)
     */
    @Override
    public Identity readObject(DataInputStream dataIn) throws IOException {
        int addressBytes = dataIn.read();

        byte[] rawAddress = new byte[addressBytes];
        dataIn.read(rawAddress);

        InetAddress address = InetAddress.getByAddress(rawAddress);

        short port = dataIn.readShort();

        Identity identity = new Identity(address, port);

        return identity;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.io.BinaryMarshaller#writeObject(java.lang.Object,
     * java.io.DataOutputStream)
     */
    @Override
    public void writeObject(Identity id, DataOutputStream dataOut)
            throws IOException {
        InetAddress address = id.getAddress();

        short port = (short) id.getPort();

        byte[] rawAddress = address.getAddress();

        int addressBytes = rawAddress.length;

        dataOut.write(addressBytes);
        dataOut.write(rawAddress);
        dataOut.writeShort(port);
    }

}
