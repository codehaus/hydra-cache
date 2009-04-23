package org.hydracache.server.harmony;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import org.hydracache.protocol.control.message.GetOperation;
import org.hydracache.protocol.control.message.GetOperationResponse;
import org.hydracache.protocol.control.message.PutOperation;
import org.hydracache.protocol.control.message.PutOperationResponse;
import org.hydracache.protocol.control.message.ResponseMessage;
import org.hydracache.protocol.control.message.VersionConflictRejection;
import org.hydracache.server.Identity;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.harmony.core.Node;
import org.hydracache.server.harmony.core.Space;
import org.hydracache.server.harmony.core.SubstanceSet;
import org.hydracache.server.harmony.jgroups.JGroupsNode;
import org.hydracache.server.harmony.storage.HarmonyDataBank;
import org.jgroups.stack.IpAddress;
import org.jmock.Expectations;
import org.jmock.Mockery;

public class AbstractMockeryTest {

    protected static Identity sourceId = new Identity(8080);

    protected static Node localNode = new JGroupsNode(new Identity(8081),
            new IpAddress(7001));

    protected static Long testHashKey = 1000L;

    public AbstractMockeryTest() {
        super();
    }

    protected static void addGetPositiveSubstanceSetExp(final Mockery context,
            final Space space) throws Exception {
        context.checking(new Expectations() {
            {
                atMost(1).of(space).findSubstancesForLocalNode();

                final SubstanceSet substanceSet = context
                        .mock(SubstanceSet.class);

                context.checking(new Expectations() {
                    {
                        atMost(1).of(substanceSet).isNeighbor(
                                with(any(Identity.class)));
                        will(returnValue(true));
                    }
                });

                will(returnValue(substanceSet));
            }
        });
    }

    protected static void addGetLocalNodeExp(Mockery context, final Space space)
            throws Exception {
        context.checking(new Expectations() {
            {
                atLeast(1).of(space).getLocalNode();
                will(returnValue(new JGroupsNode(sourceId, new IpAddress())));
            }
        });
    }

    protected static void addBroadcastResponseExp(Mockery context,
            final Space space) throws Exception {
        context.checking(new Expectations() {
            {
                one(space).broadcast(with(any(ResponseMessage.class)));
            }
        });
    }

    protected static void addLocalPutExp(Mockery context,
            final HarmonyDataBank dataBank) throws Exception {
        context.checking(new Expectations() {
            {
                one(dataBank).putLocally(with(any(Data.class)));
            }
        });
    }

    protected static void addLocalGetExp(Mockery context,
            final HarmonyDataBank dataBank, final Data testData)
            throws Exception {
        context.checking(new Expectations() {
            {
                one(dataBank).getLocally(with(any(Long.class)));
                will(returnValue(testData));
            }
        });
    }

    protected static void addLocalGetNothingExp(Mockery context,
            final HarmonyDataBank dataBank) throws Exception {
        context.checking(new Expectations() {
            {
                one(dataBank).getLocally(with(any(Long.class)));
                will(returnValue(null));
            }
        });
    }

    protected static void addFailedReliablePutExp(final Mockery context,
            final Data data, final Space space) throws Exception {
        context.checking(new Expectations() {
            {
                // only one help was provided
                Collection<PutOperationResponse> putOperationResponses = Arrays
                        .asList(new PutOperationResponse(sourceId, UUID
                                .randomUUID()));

                one(space).broadcast(with(any(PutOperation.class)));
                will(returnValue(putOperationResponses));
            }
        });
    }

    protected static void addSuccessReliablePutExp(final Mockery context,
            final Data data, final Space space) throws Exception {
        context.checking(new Expectations() {
            {
                Collection<PutOperationResponse> putOperationResponses = Arrays
                        .asList(new PutOperationResponse(sourceId, UUID
                                .randomUUID()), new PutOperationResponse(
                                sourceId, UUID.randomUUID()));

                one(space).broadcast(with(any(PutOperation.class)));
                will(returnValue(putOperationResponses));
            }
        });
    }

    protected static void addGetNullExp(final Mockery context, final Data data,
            final Space space) throws Exception {
        context.checking(new Expectations() {
            {
                Collection<GetOperationResponse> putOperationResponses = Arrays
                        .asList();

                one(space).broadcast(with(any(GetOperation.class)));
                will(returnValue(putOperationResponses));
            }
        });
    }

    protected static void addFailedReliableGetExp(final Mockery context,
            final Data data, final Space space) throws Exception {
        context.checking(new Expectations() {
            {
                Collection<GetOperationResponse> putOperationResponses = Arrays
                        .asList();

                one(space).broadcast(with(any(GetOperation.class)));
                will(returnValue(putOperationResponses));
            }
        });
    }

    protected static void addSuccessReliableGetExp(final Mockery context,
            final Data data, final Space space) throws Exception {
        context.checking(new Expectations() {
            {
                Collection<GetOperationResponse> putOperationResponses = Arrays
                        .asList(new GetOperationResponse(sourceId, UUID
                                .randomUUID(), data), new GetOperationResponse(
                                sourceId, UUID.randomUUID(), data));

                one(space).broadcast(with(any(GetOperation.class)));
                will(returnValue(putOperationResponses));
            }
        });
    }

    protected static void addBroadcastRejectionExp(Mockery context,
            final Space space) throws Exception {
        context.checking(new Expectations() {
            {
                one(space).broadcast(with(any(VersionConflictRejection.class)));
            }
        });
    }

    protected static void addRejectedPutExp(final Mockery context,
            final Data data, final Space space) throws Exception {
        context.checking(new Expectations() {
            {
                Collection<ResponseMessage> putOperationResponses = Arrays
                        .asList(new PutOperationResponse(sourceId, UUID
                                .randomUUID()), new VersionConflictRejection(
                                sourceId, UUID.randomUUID()));

                one(space).broadcast(with(any(PutOperation.class)));
                will(returnValue(putOperationResponses));
            }
        });
    }

}