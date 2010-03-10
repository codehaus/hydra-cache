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
package org.hydracache.server.data.versioning;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.commons.lang.Validate;
import org.hydracache.server.Identity;

/**
 * @author David Dossot (david@dossot.net)
 * 
 */
public class IncrementVersionFactory extends AbstractVersionFactoryMarshaller {

    @Override
    public Version create(final Identity nodeId) {
        return new Increment(nodeId);
    }

    @Override
    public Version readObject(final DataInputStream dataIn) throws IOException {
        Validate.notNull(dataIn, "dataIn can not be null");

        final Identity nodeId = getIdentityMarshaller().readObject(dataIn);
        final long value = dataIn.readLong();

        return new Increment(nodeId, value);
    }

    @Override
    public void writeObject(final Version version,
            final DataOutputStream dataOut) throws IOException {

        Validate.isTrue(version instanceof Increment,
                "version must be non null and an instance of Increment");

        Validate.notNull(dataOut, "dataOut can not be null");

        final Increment increment = (Increment) version;
        getIdentityMarshaller().writeObject(increment.getNodeId(), dataOut);
        dataOut.writeLong(increment.getValue());
    }

}
