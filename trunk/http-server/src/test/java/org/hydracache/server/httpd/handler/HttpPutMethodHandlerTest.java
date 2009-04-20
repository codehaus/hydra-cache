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
package org.hydracache.server.httpd.handler;

import org.hydracache.io.Marshaller;
import org.hydracache.protocol.data.codec.DefaultProtocolDecoder;
import org.hydracache.protocol.data.codec.DefaultProtocolEncoder;
import org.hydracache.protocol.data.marshaller.MessageMarshallerFactory;
import org.hydracache.protocol.data.message.DataMessage;
import org.hydracache.server.data.versioning.IncrementVersionFactory;
import org.hydracache.server.data.versioning.Version;
import org.hydracache.server.data.versioning.VersionFactory;
import org.hydracache.server.harmony.jgroups.JGroupsNode;
import org.hydracache.server.harmony.storage.HarmonyDataBank;
import org.jgroups.stack.IpAddress;
import org.junit.Test;

/**
 * @author nzhu
 * 
 */
public class HttpPutMethodHandlerTest extends AbstractHttpMethodHandlerTest {
    HttpPutMethodHandler handler;

    @Override
    public void initialize() {
        super.initialize();

        handler = createHandler(versionFactoryMarshaller,
                versionFactoryMarshaller);
    }

    private HttpPutMethodHandler createHandler(
            final VersionFactory versionFactory,
            final Marshaller<Version> versionMarshaller) {
        final HarmonyDataBank dataBank = context.mock(HarmonyDataBank.class);

        DefaultProtocolEncoder messageEncoder = new DefaultProtocolEncoder(
                new MessageMarshallerFactory(versionFactoryMarshaller));
        DefaultProtocolDecoder messageDecoder = new DefaultProtocolDecoder(
                new MessageMarshallerFactory(versionMarshaller));

        final HttpPutMethodHandler handler = new HttpPutMethodHandler(
                versionFactory, dataBank, hashFunction, messageEncoder,
                messageDecoder, new JGroupsNode(localId, new IpAddress(7000)));

        return handler;
    }

    @Test
    public void ensureLocalVersionConflictDetectionIgnoresNull()
            throws Exception {
        {
            addNullReturnedFromLocalGetExp(handler.dataBank);
            addSuccessLocalPutExp(handler.dataBank);
        }

        {
            addSetCreatedStatusCodeExp(response);
            addSetBinaryDataEntityExp(response);
        }

        DataMessage validNewDataMessage = createValidNewDataMessage();

        handler.processDataMessage(response, testKey, validNewDataMessage);
    }

    private DataMessage createValidNewDataMessage() {
        DataMessage incomingDataMsg = new DataMessage();
        incomingDataMsg.setBlob(generateRandomBytes());
        incomingDataMsg.setVersion(new IncrementVersionFactory().createNull());
        return incomingDataMsg;
    }

    @Test
    public void ensureLocalVersionConflictDetection() throws Exception {
        {
            addConflictLocalGetExp(handler.dataBank);
        }

        {
            addSetConflictStatusCodeExp(response);
            addSetStringMessageEntityExp(response);
        }

        DataMessage validNewDataMessage = createValidNewDataMessage();

        handler.processDataMessage(response, testKey, validNewDataMessage);
    }

    @Test
    public void ensureNewVersionIsCreatedIfGivenVersionIsNull()
            throws Exception {
        {
            addNullReturnedFromLocalGetExp(handler.dataBank);
            addSuccessLocalPutExp(handler.dataBank);
        }

        {
            addSetCreatedStatusCodeExp(response);
            addSetBinaryDataEntityExp(response);
        }

        DataMessage nullVersionDataMessage = createValidNewDataMessage();
        nullVersionDataMessage.setVersion(null);

        handler.processDataMessage(response, testKey, nullVersionDataMessage);
    }

    @Test
    public void ensureStatusOkIsReturnedForUpdate() throws Exception {
        {
            addSuccessfulLocalGetExp(handler.dataBank);
            addSuccessLocalPutExp(handler.dataBank);
        }

        {
            addSetOkStatusCodeExp(response);
            addSetBinaryDataEntityExp(response);
        }

        DataMessage validUpdateDataMessage = createValidUpdateDataMessage();

        handler.processDataMessage(response, testKey, validUpdateDataMessage);
    }

    private DataMessage createValidUpdateDataMessage() {
        DataMessage incomingDataMsg = new DataMessage();
        incomingDataMsg.setBlob(generateRandomBytes());
        incomingDataMsg.setVersion(new IncrementVersionFactory()
                .create(localId).incrementFor(localId));
        return incomingDataMsg;
    }

}
