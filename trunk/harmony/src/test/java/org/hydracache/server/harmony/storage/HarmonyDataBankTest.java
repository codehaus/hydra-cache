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
import static org.junit.Assert.assertNull;

import org.hydracache.server.data.resolver.ArbitraryResolver;
import org.hydracache.server.data.resolver.ConflictResolver;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.data.storage.DataBank;
import org.hydracache.server.data.storage.EhcacheDataBank;
import org.hydracache.server.data.versioning.VersionConflictException;
import org.hydracache.server.harmony.AbstractMockeryTest;
import org.hydracache.server.harmony.core.Space;
import org.hydracache.server.harmony.test.TestDataGenerator;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author nzhu
 * 
 */
public class HarmonyDataBankTest extends AbstractMockeryTest {

    private Mockery context;

    private ConflictResolver conflictResolver = new ArbitraryResolver();

    private DataBank localDataBank = new EhcacheDataBank(conflictResolver);
    
    @Before
    public void setup(){
        context = new Mockery();
    }
    
    @After
    public void teardown(){
        context.assertIsSatisfied();
    }

    @Test
    public void ensureNegativeWorRCanBeHandledAsOne() throws Exception {
        final Data data = TestDataGenerator.createRandomData();

        Space space = context.mock(Space.class);

        HarmonyDataBank dataBank = new HarmonyDataBank(localDataBank,
                conflictResolver, space);

        dataBank.setExpectedWrites(-5);
        dataBank.setExpectedReads(-5);

        dataBank.put(data);

        assertEquals("Data is incorrect with R, W=-5 put and get operations",
                data, dataBank.get(data.getKeyHash()));
    }

    @Test
    public void ensureZeroWorRCanBeHandledAsOne() throws Exception {
        final Data data = TestDataGenerator.createRandomData();

        Space space = context.mock(Space.class);

        HarmonyDataBank dataBank = new HarmonyDataBank(localDataBank,
                conflictResolver, space);

        dataBank.setExpectedWrites(0);
        dataBank.setExpectedReads(0);

        dataBank.put(data);

        assertEquals("Data is incorrect with R, W=0 put and get operations",
                data, dataBank.get(data.getKeyHash()));
    }

    @Test
    public void ensureDataBankCanBeConfiguredToWriteLocally() throws Exception {
        final Data data = TestDataGenerator.createRandomData();

        Space space = context.mock(Space.class);

        HarmonyDataBank dataBank = new HarmonyDataBank(localDataBank,
                conflictResolver, space);

        dataBank.setExpectedWrites(1);

        dataBank.put(data);

        assertEquals("Data is incorrect with W=1 put and get operations", data,
                dataBank.getLocally(data.getKeyHash()));
    }

    @Test
    public void ensureDataBankCanBeConfiguredToReadLocally() throws Exception {
        final Data data = TestDataGenerator.createRandomData();

        Space space = context.mock(Space.class);

        HarmonyDataBank dataBank = new HarmonyDataBank(localDataBank,
                conflictResolver, space);

        dataBank.putLocally(data);
        dataBank.setExpectedReads(1);

        assertEquals("Data is incorrect with R=1 put and get operations", data,
                dataBank.get(data.getKeyHash()));
    }

    @Test
    public void ensurePassThroughLocalPutAndGet() throws Exception {
        final Data data = TestDataGenerator.createRandomData();

        Space space = context.mock(Space.class);

        HarmonyDataBank dataBank = new HarmonyDataBank(localDataBank,
                conflictResolver, space);

        dataBank.putLocally(data);

        assertEquals("Data is incorrect after local put and get operations",
                data, dataBank.getLocally(data.getKeyHash()));
    }

    @Test
    public void ensureGetWithNoResultShouldReturnNull() throws Exception {
        final Data data = TestDataGenerator.createRandomData();

        Space space = context.mock(Space.class);

        {
            addGetLocalNodeExp(context, space);
            addEmptyReliableGetExp(context, data, space);
        }

        HarmonyDataBank dataBank = new HarmonyDataBank(localDataBank,
                conflictResolver, space);

        assertNull("Should return null", dataBank.get(1000L));
    }

    @Test
    public void ensureEmptyLocalBankShouldBroadcaseWhenRIs1() throws Exception {
        final Data data = TestDataGenerator.createRandomData();

        Space space = context.mock(Space.class);

        {
            addGetLocalNodeExp(context, space);
            addSuccessReliableGetExp(context, data, space);
        }

        HarmonyDataBank dataBank = new HarmonyDataBank(localDataBank,
                conflictResolver, space);
        dataBank.setExpectedReads(1);

        Data receivedData = dataBank.get(testHashKey);

        assertEquals("Data returned by GET is incorrect", data, receivedData);
    }

    @Test(expected = ReliableDataStorageException.class)
    public void ensureNotReliableGetIsDetected() throws Exception {
        final Data data = TestDataGenerator.createRandomData();

        Space space = context.mock(Space.class);

        {
            addGetLocalNodeExp(context, space);
            addFailedReliableGetExp(context, data, space);
        }

        localDataBank.put(data);

        HarmonyDataBank dataBank = new HarmonyDataBank(localDataBank,
                conflictResolver, space);
        dataBank.setExpectedReads(2);

        dataBank.get(data.getKeyHash());
    }

    @Test
    public void getShouldBroadcastToSpace() throws Exception {
        final Data data = TestDataGenerator.createRandomData();

        Space space = context.mock(Space.class);

        {
            addGetLocalNodeExp(context, space);
            addSuccessReliableGetExp(context, data, space);
        }

        HarmonyDataBank dataBank = new HarmonyDataBank(localDataBank,
                conflictResolver, space);

        Data receivedData = dataBank.get(testHashKey);

        assertEquals("Data returned by GET is incorrect", data, receivedData);

        assertEquals(
                "Data bank should have updated its local cache after a reliable GET",
                receivedData, dataBank.getLocally(receivedData.getKeyHash()));
    }

    @Test
    public void putShouldBroadcastToSpace() throws Exception {
        final Data data = TestDataGenerator.createRandomData();

        Space space = context.mock(Space.class);

        {
            addGetLocalNodeExp(context, space);
            addSuccessReliablePutExp(context, data, space);
        }

        HarmonyDataBank dataBank = new HarmonyDataBank(localDataBank,
                conflictResolver, space);

        dataBank.put(data);

        assertEquals("Data should exist in local bank", data, localDataBank
                .get(data.getKeyHash()));
    }

    @Test(expected = ReliableDataStorageException.class)
    public void putWithNotEnoughHelpShouldFail() throws Exception {
        final Data data = TestDataGenerator.createRandomData();

        Space space = context.mock(Space.class);

        {
            addGetLocalNodeExp(context, space);
            addFailedReliablePutExp(context, data, space);
        }

        HarmonyDataBank dataBank = new HarmonyDataBank(localDataBank,
                conflictResolver, space);

        dataBank.put(data);
    }

    @Test(expected = VersionConflictException.class)
    public void ensurePutShouldBeRejectedIfRejectionIsReceived()
            throws Exception {
        final Data data = TestDataGenerator.createRandomData();

        Space space = context.mock(Space.class);

        {
            addGetLocalNodeExp(context, space);
            addRejectedPutExp(context, data, space);
        }

        HarmonyDataBank dataBank = new HarmonyDataBank(localDataBank,
                conflictResolver, space);

        dataBank.put(data);
    }

}
