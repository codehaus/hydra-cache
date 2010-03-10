package org.hydracache.server.harmony.handler;

import org.hydracache.protocol.control.message.DeleteOperation;
import org.hydracache.server.harmony.AbstractMockeryTest;
import org.hydracache.server.harmony.core.Space;
import org.hydracache.server.harmony.membership.MembershipRegistry;
import org.hydracache.server.harmony.storage.HarmonyDataBank;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

public class DeleteOperationHandlerTest extends AbstractMockeryTest {
    private Mockery context = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    public String storageContext = "testContext";

    @Test
    public void ensureGetReturnsAndBroadcastLocalData() throws Exception {
        final HarmonyDataBank dataBank = context.mock(HarmonyDataBank.class);

        {
            addDeleteLocallyExp(context, dataBank);
        }

        final Space space = context.mock(Space.class);

        {
            addGetPositiveSubstanceSetExp(context, space);
            addGetLocalNodeExp(context, space);
            addBroadcastResponseExp(context, space);
        }

        MembershipRegistry memberRegistry = new MembershipRegistry(localNode);
        memberRegistry.setSpace(space);

        DeleteOperationHandler handler = new DeleteOperationHandler(space,
                memberRegistry, dataBank);

        DeleteOperation operation = new DeleteOperation(sourceId,
                storageContext, testHashKey);

        handler.handle(operation);

        context.assertIsSatisfied();
    }

}
