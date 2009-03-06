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
package org.hydracache.server.harmony.storage;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import org.hydracache.protocol.control.message.GetOperation;
import org.hydracache.protocol.control.message.GetOperationResponse;
import org.hydracache.protocol.control.message.PutOperation;
import org.hydracache.protocol.control.message.PutOperationResponse;
import org.hydracache.server.Identity;
import org.hydracache.server.data.resolver.ArbitraryResolver;
import org.hydracache.server.data.resolver.ConflictResolver;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.data.storage.DataBank;
import org.hydracache.server.data.storage.EhcacheDataBank;
import org.hydracache.server.harmony.core.Space;
import org.hydracache.server.harmony.jgroups.JGroupsNode;
import org.hydracache.server.harmony.test.TestDataGenerator;
import org.jgroups.stack.IpAddress;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

/**
 * @author nzhu
 * 
 */
public class HarmonyDataBankTest {

    private static Identity defaultSource;

    private Mockery context = new Mockery();

    @Before
    public void setup() throws Exception {
        defaultSource = new Identity(8080);
    }

    @Test
    public void ensurePassThroughLocalPutAndGet() throws Exception {
        final Data data = TestDataGenerator.createRandomData();

        DataBank localDataBank = new EhcacheDataBank(null);

        HarmonyDataBank dataBank = new HarmonyDataBank(localDataBank, null,
                null);

        dataBank.putLocally(data);

        assertEquals("Data is incorrect after local put and get operations",
                data, dataBank.getLocally(data.getKeyHash()));
    }

    @Test(expected=ReliableDataStorageException.class)
    public void ensureNotReliableGetIsDetected() throws Exception {
        final Data data = TestDataGenerator.createRandomData();

        Space space = context.mock(Space.class);

        {
            addGetLocalNodeExp(space);
            addFailedReliableGetExp(data, space);
        }

        ConflictResolver conflictResolver = new ArbitraryResolver();

        DataBank localDataBank = new EhcacheDataBank(conflictResolver);

        HarmonyDataBank dataBank = new HarmonyDataBank(localDataBank,
                conflictResolver, space);

        dataBank.get(1000L);
    }

    private void addFailedReliableGetExp(final Data data, final Space space)
            throws Exception {
        context.checking(new Expectations() {
            {
                Collection<GetOperationResponse> putOperationResponses = Arrays
                        .asList();

                one(space).broadcast(with(any(GetOperation.class)));
                will(returnValue(putOperationResponses));
            }
        });
    }

    @Test
    public void getShouldBroadcastToSpace() throws Exception {
        final Data data = TestDataGenerator.createRandomData();

        Space space = context.mock(Space.class);

        {
            addGetLocalNodeExp(space);
            addSuccessReliableGetExp(data, space);
        }

        ConflictResolver conflictResolver = new ArbitraryResolver();

        DataBank localDataBank = new EhcacheDataBank(conflictResolver);

        HarmonyDataBank dataBank = new HarmonyDataBank(localDataBank,
                conflictResolver, space);

        Data receivedData = dataBank.get(1000L);

        assertEquals("Data returned by GET is incorrect", data, receivedData);

        context.assertIsSatisfied();
    }

    private void addSuccessReliableGetExp(final Data data, final Space space)
            throws Exception {
        context.checking(new Expectations() {
            {
                Collection<GetOperationResponse> putOperationResponses = Arrays
                        .asList(new GetOperationResponse(defaultSource, UUID
                                .randomUUID(), data), new GetOperationResponse(
                                defaultSource, UUID.randomUUID(), data));

                one(space).broadcast(with(any(GetOperation.class)));
                will(returnValue(putOperationResponses));
            }
        });
    }

    @Test
    public void putShouldBroadcastToSpace() throws Exception {
        final Data data = TestDataGenerator.createRandomData();

        Space space = context.mock(Space.class);

        {
            addGetLocalNodeExp(space);
            addSuccessReliablePutExp(data, space);
        }

        ConflictResolver conflictResolver = new ArbitraryResolver();

        DataBank localDataBank = new EhcacheDataBank(conflictResolver);

        HarmonyDataBank dataBank = new HarmonyDataBank(localDataBank,
                conflictResolver, space);

        dataBank.put(data);

        assertEquals("Data should exist in local bank", data, localDataBank
                .get(data.getKeyHash()));

        context.assertIsSatisfied();
    }

    @Test(expected = ReliableDataStorageException.class)
    public void putWithNotEnoughHelpShouldFail() throws Exception {
        final Data data = TestDataGenerator.createRandomData();

        Space space = context.mock(Space.class);

        {
            addGetLocalNodeExp(space);
            addFailedRequestHelpExp(data, space);
        }

        ConflictResolver conflictResolver = new ArbitraryResolver();

        DataBank localDataBank = new EhcacheDataBank(conflictResolver);

        HarmonyDataBank dataBank = new HarmonyDataBank(localDataBank,
                conflictResolver, space);

        dataBank.put(data);
    }

    private void addGetLocalNodeExp(final Space space) throws Exception {
        context.checking(new Expectations() {
            {
                one(space).getLocalNode();
                will(returnValue(new JGroupsNode(new Identity(8080),
                        new IpAddress())));
            }
        });
    }

    private void addSuccessReliablePutExp(final Data data, final Space space)
            throws Exception {
        context.checking(new Expectations() {
            {
                Collection<PutOperationResponse> putOperationResponses = Arrays
                        .asList(new PutOperationResponse(defaultSource, UUID
                                .randomUUID()), new PutOperationResponse(
                                defaultSource, UUID.randomUUID()));

                one(space).broadcast(with(any(PutOperation.class)));
                will(returnValue(putOperationResponses));
            }
        });
    }

    private void addFailedRequestHelpExp(final Data data, final Space space)
            throws Exception {
        context.checking(new Expectations() {
            {
                // only one help was provided
                Collection<PutOperationResponse> putOperationResponses = Arrays
                        .asList(new PutOperationResponse(defaultSource, UUID
                                .randomUUID()));

                one(space).broadcast(with(any(PutOperation.class)));
                will(returnValue(putOperationResponses));
            }
        });
    }

}
