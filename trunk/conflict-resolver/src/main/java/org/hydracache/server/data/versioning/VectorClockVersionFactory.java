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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.hydracache.server.Identity;

/**
 * @author David Dossot (david@dossot.net)
 */
public final class VectorClockVersionFactory extends
        AbstractVersionFactoryMarshaller {

    @Override
    public Version create(final Identity nodeId) {
        return new VectorClock(nodeId);
    }

    @Override
    public Version readObject(final DataInputStream dataIn) throws IOException {
        Validate.notNull(dataIn, "dataIn can not be null");

        final int entriesCount = dataIn.readInt();
        final List<VectorClockEntry> vectorClockEntries = new ArrayList<VectorClockEntry>();

        for (int i = 0; i < entriesCount; i++) {
            final Identity nodeId = getIdentityMarshaller().readObject(dataIn);
            final long value = dataIn.readLong();
            final long timeStamp = dataIn.readLong();
            vectorClockEntries.add(new VectorClockEntry(nodeId, value,
                    timeStamp));
        }

        return new VectorClock(vectorClockEntries);
    }

    @Override
    public void writeObject(final Version version,
            final DataOutputStream dataOut) throws IOException {

        Validate.isTrue(version instanceof VectorClock,
                "version must be non null and an instance of VectorClock");

        Validate.notNull(dataOut, "dataOut can not be null");

        final VectorClock vectorClock = (VectorClock) version;
        final List<VectorClockEntry> vectorClockEntries = vectorClock
                .getEntries();

        dataOut.writeInt(vectorClockEntries.size());

        for (final VectorClockEntry vce : vectorClockEntries) {
            getIdentityMarshaller().writeObject(vce.getNodeId(), dataOut);
            dataOut.writeLong(vce.getValue());
            dataOut.writeLong(vce.getTimestamp());
        }
    }

}
