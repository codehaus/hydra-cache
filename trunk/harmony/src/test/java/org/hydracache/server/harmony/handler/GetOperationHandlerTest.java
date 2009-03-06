package org.hydracache.server.harmony.handler;

import org.hydracache.protocol.control.message.GetOperation;
import org.hydracache.protocol.control.message.GetOperationResponse;
import org.hydracache.server.Identity;
import org.hydracache.server.harmony.core.Node;
import org.hydracache.server.harmony.core.Space;
import org.hydracache.server.harmony.jgroups.JGroupsNode;
import org.hydracache.server.harmony.storage.HarmonyDataBank;
import org.hydracache.server.harmony.test.TestDataGenerator;
import org.jgroups.stack.IpAddress;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

public class GetOperationHandlerTest {
    private Mockery context = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    private Identity sourceId = new Identity(8080);

    private Node localNode = new JGroupsNode(new Identity(8081), new IpAddress(
            7001));

    private Long testHashKey = 1000L;

    @Test
    public void ensureGetReturnsAndBroadcastLocalData() throws Exception {
        final HarmonyDataBank dataBank = context.mock(HarmonyDataBank.class);

        {
            addLocalGetExp(dataBank);
        }

        final Space space = context.mock(Space.class);

        {
            addGetLocalNodeExp(space);
            addBroadcastGetResponseExp(space);
        }

        GetOperationHandler handler = new GetOperationHandler(space, dataBank);

        GetOperation getOperation = new GetOperation(sourceId, testHashKey);

        handler.handle(getOperation);

        context.assertIsSatisfied();
    }

    private void addLocalGetExp(final HarmonyDataBank dataBank) throws Exception {
        context.checking(new Expectations() {
            {
                one(dataBank).getLocally(with(testHashKey));
                will(returnValue(TestDataGenerator.createRandomData()));
            }
        });
    }

    private void addGetLocalNodeExp(final Space space) {
        context.checking(new Expectations() {
            {
                one(space).getLocalNode();
                will(returnValue(localNode));
            }
        });
    }

    private void addBroadcastGetResponseExp(final Space space) throws Exception {
        context.checking(new Expectations() {
            {
                one(space).broadcast(with(any(GetOperationResponse.class)));
            }
        });
    }

}
