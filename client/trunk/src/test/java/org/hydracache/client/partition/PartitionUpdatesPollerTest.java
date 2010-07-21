package org.hydracache.client.partition;

import org.hydracache.client.HydraCacheAdminClient;
import org.hydracache.server.Identity;
import org.junit.Test;

import java.util.Arrays;
import java.util.Observer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by nick.zhu
 */
public class PartitionUpdatesPollerTest {

    @Test
    public void ensurePollerIsStoppable() throws InterruptedException {
        HydraCacheAdminClient adminClient = mock(HydraCacheAdminClient.class);
        when(adminClient.isRunning()).thenReturn(true).thenReturn(false);
        Observer observer = mock(Observer.class);

        PartitionUpdatesPoller poller = new PartitionUpdatesPoller(Arrays.asList(new Identity(80)), 5000);

        poller.setAdminClient(adminClient);
        poller.addListener(observer);

        poller.start();

        Thread.sleep(10);

        assertTrue("Should be running", poller.isAlive());

        poller.shutdown();

        Thread.sleep(10);

        assertFalse("Should not be running", poller.isAlive());

    }

}
