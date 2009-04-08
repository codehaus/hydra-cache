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
package org.hydracache.server.harmony.handler;

import java.util.Arrays;
import java.util.Collection;

import org.hydracache.protocol.control.message.PutOperation;
import org.hydracache.server.data.resolver.ArbitraryResolver;
import org.hydracache.server.data.resolver.ConflictResolver;
import org.hydracache.server.data.resolver.DefaultResolutionResult;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.harmony.AbstractMockeryTest;
import org.hydracache.server.harmony.core.Space;
import org.hydracache.server.harmony.core.SubstanceSet;
import org.hydracache.server.harmony.membership.MembershipRegistry;
import org.hydracache.server.harmony.storage.HarmonyDataBank;
import org.hydracache.server.harmony.test.TestDataGenerator;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

public class PutOperationHandlerTest extends AbstractMockeryTest {
    private Mockery context = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    @Test
    public void ensureVersionConflictTriggersRejection() throws Exception {
        Space space = mockSpaceToReject(context);

        MembershipRegistry memberRegistry = new MembershipRegistry(localNode);
        memberRegistry.setSpace(space);

        ControlMessageHandler handler = new PutOperationHandler(space,
                memberRegistry, mockDataBankToReturnConflict(),
                new ArbitraryResolver());

        Data testData = TestDataGenerator.createRandomData();

        PutOperation putOperation = new PutOperation(sourceId, testData);
        handler.handle(putOperation);

        context.assertIsSatisfied();
    }

    private HarmonyDataBank mockDataBankToReturnConflict() throws Exception {
        final HarmonyDataBank dataBank = context.mock(HarmonyDataBank.class);
        {
            addLocalGetExp(context, dataBank, TestDataGenerator
                    .createRandomData());
        }
        return dataBank;
    }

    private static Space mockSpaceToReject(Mockery context) throws Exception {
        final Space space = context.mock(Space.class);
        {
            addGetPositiveSubstanceSetExp(context, space);
            addGetLocalNodeExp(context, space);
            addBroadcastRejectionExp(context, space);
        }

        return space;
    }

    @Test
    public void ensureBrandNewDataIsHandledCorrectly() throws Exception {
        Space space = mockSpaceToRespond(context);

        MembershipRegistry memberRegistry = new MembershipRegistry(localNode);
        memberRegistry.setSpace(space);

        Data testData = TestDataGenerator.createRandomData();

        ControlMessageHandler handler = new PutOperationHandler(space,
                memberRegistry,
                mockDataBankToPutBrandNewData(context, testData),
                new ArbitraryResolver());

        PutOperation putOperation = new PutOperation(sourceId, testData);
        handler.handle(putOperation);

        context.assertIsSatisfied();
    }

    private static HarmonyDataBank mockDataBankToPutBrandNewData(
            Mockery context, Data testData) throws Exception {
        final HarmonyDataBank dataBank = context.mock(HarmonyDataBank.class);
        {
            addLocalPutExp(context, dataBank);
            addLocalGetNothingExp(context, dataBank);
        }
        return dataBank;
    }

    @SuppressWarnings("unchecked")
    @Test(expected = IllegalArgumentException.class)
    public void ensureMultipleLiveConflictResolvementResultCanBeHandled()
            throws Exception {
        final Data existingData = TestDataGenerator.createRandomData();

        final ConflictResolver resolverWithConflictResult = context
                .mock(ConflictResolver.class);

        context.checking(new Expectations() {
            {
                one(resolverWithConflictResult).resolve(
                        with(any(Collection.class)));
                will(returnValue(new DefaultResolutionResult(Arrays.asList(
                        existingData, TestDataGenerator.createRandomData()),
                        Arrays.asList(existingData))));
            }
        });

        Space space = mockSpaceToRespond(context);

        MembershipRegistry memberRegistry = new MembershipRegistry(localNode);
        memberRegistry.setSpace(space);

        ControlMessageHandler handler = new PutOperationHandler(space,
                memberRegistry, mockDataBankToGetAndPut(context, existingData),
                resolverWithConflictResult);

        Data newData = generateValidNewData(existingData);

        PutOperation putOperation = new PutOperation(sourceId, newData);
        handler.handle(putOperation);

        context.assertIsSatisfied();
    }

    @Test
    public void putRequestFromWithinNeighborhoodShouldBeHonored()
            throws Exception {
        Data existingData = TestDataGenerator.createRandomData();

        Space space = mockSpaceToRespond(context);

        MembershipRegistry memberRegistry = new MembershipRegistry(localNode);
        memberRegistry.setSpace(space);

        ControlMessageHandler handler = new PutOperationHandler(space,
                memberRegistry, mockDataBankToGetAndPut(context, existingData),
                new ArbitraryResolver());

        Data newData = generateValidNewData(existingData);

        PutOperation putOperation = new PutOperation(sourceId, newData);
        handler.handle(putOperation);

        context.assertIsSatisfied();
    }

    private Data generateValidNewData(final Data existingData) {
        Data newData = new Data(existingData.getKeyHash(), existingData
                .getVersion().incrementFor(sourceId), existingData.getContent());
        return newData;
    }

    @Test
    public void putRequestFromOutsideOfNeighborhoodShouldBeIgnored()
            throws Exception {
        final Space space = context.mock(Space.class);

        context.checking(new Expectations() {
            {
                one(space).findSubstancesForLocalNode();
                will(returnValue(new SubstanceSet()));
            }
        });

        HarmonyDataBank doNothingDatabank = context.mock(HarmonyDataBank.class);

        MembershipRegistry memberRegistry = new MembershipRegistry(localNode);
        memberRegistry.setSpace(space);

        ControlMessageHandler handler = new PutOperationHandler(space,
                memberRegistry, doNothingDatabank, new ArbitraryResolver());

        handler.handle(new PutOperation(sourceId, TestDataGenerator
                .createRandomData()));

        context.assertIsSatisfied();
    }

    private static Space mockSpaceToRespond(Mockery context) throws Exception {
        final Space space = context.mock(Space.class);
        {
            addGetPositiveSubstanceSetExp(context, space);
            addGetLocalNodeExp(context, space);
            addBroadcastResponseExp(context, space);
        }
        return space;
    }

    private static HarmonyDataBank mockDataBankToGetAndPut(Mockery context,
            Data testData) throws Exception {
        final HarmonyDataBank dataBank = context.mock(HarmonyDataBank.class);
        {
            addLocalPutExp(context, dataBank);
            addLocalGetExp(context, dataBank, testData);
        }
        return dataBank;
    }
}
