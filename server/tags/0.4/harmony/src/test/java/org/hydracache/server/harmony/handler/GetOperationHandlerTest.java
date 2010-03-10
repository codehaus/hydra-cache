package org.hydracache.server.harmony.handler;

import org.hydracache.protocol.control.message.GetOperation;
import org.hydracache.server.harmony.AbstractMockeryTest;
import org.hydracache.server.harmony.core.Space;
import org.hydracache.server.harmony.membership.MembershipRegistry;
import org.hydracache.server.harmony.storage.HarmonyDataBank;
import org.hydracache.server.harmony.test.TestDataGenerator;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

public class GetOperationHandlerTest extends AbstractMockeryTest {
    private Mockery context = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
    private String storageContext = "testContext";

    @Test
    public void ensureGetReturnsAndBroadcastLocalData() throws Exception {
        final HarmonyDataBank dataBank = context.mock(HarmonyDataBank.class);

        {
            addLocalGetExp(context, dataBank, TestDataGenerator
                    .createRandomData(), storageContext);
        }

        final Space space = context.mock(Space.class);

        {
            addGetPositiveSubstanceSetExp(context, space);
            addGetLocalNodeExp(context, space);
            addBroadcastResponseExp(context, space);
        }
        
        MembershipRegistry memberRegistry = new MembershipRegistry(localNode);
        memberRegistry.setSpace(space);

        GetOperationHandler handler = new GetOperationHandler(space, memberRegistry, dataBank);

        GetOperation getOperation = new GetOperation(sourceId, storageContext, testHashKey);

        handler.handle(getOperation);

        context.assertIsSatisfied();
    }

}
