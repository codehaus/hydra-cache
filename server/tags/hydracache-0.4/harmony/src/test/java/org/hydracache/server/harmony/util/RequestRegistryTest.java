package org.hydracache.server.harmony.util;

import java.util.UUID;

import org.hydracache.concurrent.SimpleResultFuture;
import org.hydracache.protocol.control.message.ResponseMessage;
import org.junit.Test;
import static org.junit.Assert.*;

public class RequestRegistryTest {

    @Test
    public void ensureTimeoutRequestsAreCleanedUp() {
        UUID randomUuid = UUID.randomUUID();

        RequestRegistry registry = givenRequestRegistryWithOneUniqueRequest(randomUuid);

        randomUuid = null;
        afterGarbageCollection();

        assertTrue("Registry should be empty", registry.isEmpty());
    }

    private RequestRegistry givenRequestRegistryWithOneUniqueRequest(
            UUID randomUuid) {
        RequestRegistry registry = new RequestRegistry();

        registry
                .register(randomUuid, new SimpleResultFuture<ResponseMessage>());
        assertNotNull(registry.retrieveResultFuture(randomUuid));
        return registry;
    }

    private void afterGarbageCollection() {
        System.gc();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
    }

}
