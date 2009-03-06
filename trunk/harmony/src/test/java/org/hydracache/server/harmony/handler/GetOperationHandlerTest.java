package org.hydracache.server.harmony.handler;

import org.hydracache.protocol.control.message.GetOperation;
import org.hydracache.server.harmony.core.Space;
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

    @Test
    public void ensureGetReturnsAndBroadcastLocalData() throws Exception {
        final HarmonyDataBank dataBank = context.mock(HarmonyDataBank.class);

        {
            addLocalGetExp(context, dataBank, TestDataGenerator
                    .createRandomData());
        }

        final Space space = context.mock(Space.class);

        {
            addGetPositiveSubstanceSetExp(context, space);
            addGetLocalNodeExp(context, space);
            addBroadcastResponseExp(context, space);
        }

        GetOperationHandler handler = new GetOperationHandler(space, dataBank);

        GetOperation getOperation = new GetOperation(sourceId, testHashKey);

        handler.handle(getOperation);

        context.assertIsSatisfied();
    }

}
