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

import org.hydracache.protocol.control.message.PutOperation;
import org.hydracache.protocol.control.message.PutOperationResponse;
import org.hydracache.server.Identity;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.data.storage.DataBank;
import org.hydracache.server.harmony.core.Space;
import org.hydracache.server.harmony.core.SubstanceSet;
import org.hydracache.server.harmony.jgroups.JGroupsNode;
import org.hydracache.server.harmony.test.TestDataGenerator;
import org.jgroups.stack.IpAddress;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

public class PutOperationHandlerTest {
    private static final int TEST_PORT = 8080;

    private Mockery context = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    @Test
    public void putRequestFromWithinNeighborhoodShouldBeHonored()
            throws Exception {
        ControlMessageHandler handler = new PutOperationHandler(
                mockSpaceToRespond(context), mockDataBankToPut(context));

        PutOperation putOperation = new PutOperation(new Identity(TEST_PORT),
                TestDataGenerator.createRandomData());
        handler.handle(putOperation);

        context.assertIsSatisfied();
    }

    public static Space mockSpaceToRespond(Mockery context) throws Exception {
        final Space space = context.mock(Space.class);
        {
            addGetPositiveSubstanceSetExp(context, space);
            addGetLocalNodeExp(context, space);
            addBroadcastPutResponseExp(context, space);
        }
        return space;
    }

    public static DataBank mockDataBankToPut(Mockery context) throws Exception {
        final DataBank dataBank = context.mock(DataBank.class);
        {
            addPutExp(context, dataBank);
        }
        return dataBank;
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

        DataBank doNothingDatabank = context.mock(DataBank.class);

        ControlMessageHandler handler = new PutOperationHandler(space,
                doNothingDatabank);

        handler.handle(new PutOperation(new Identity(TEST_PORT),
                TestDataGenerator.createRandomData()));

        context.assertIsSatisfied();
    }

    private static void addGetPositiveSubstanceSetExp(final Mockery context,
            final Space space) throws Exception {
        context.checking(new Expectations() {
            {
                one(space).findSubstancesForLocalNode();

                final SubstanceSet substanceSet = context
                        .mock(SubstanceSet.class);

                context.checking(new Expectations() {
                    {
                        one(substanceSet).isNeighbor(with(any(Identity.class)));
                        will(returnValue(true));
                    }
                });

                will(returnValue(substanceSet));
            }
        });
    }

    private static void addGetLocalNodeExp(Mockery context, final Space space)
            throws Exception {
        context.checking(new Expectations() {
            {
                one(space).getLocalNode();
                will(returnValue(new JGroupsNode(new Identity(TEST_PORT),
                        new IpAddress())));
            }
        });
    }

    private static void addBroadcastPutResponseExp(Mockery context,
            final Space space) throws Exception {
        context.checking(new Expectations() {
            {
                one(space).broadcast(with(any(PutOperationResponse.class)));
            }
        });
    }

    private static void addPutExp(Mockery context, final DataBank dataBank)
            throws Exception {
        context.checking(new Expectations() {
            {
                one(dataBank).put(with(any(Data.class)));
            }
        });
    }

}
