package org.hydracache.server.harmony.health;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.hydracache.server.Identity;
import org.hydracache.server.harmony.core.Node;
import org.hydracache.server.harmony.core.Space;
import org.hydracache.server.harmony.health.HealthMonitor;
import org.hydracache.server.harmony.jgroups.JGroupsNode;
import org.jgroups.stack.IpAddress;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HealthMonitorTest {

    private static final int TEST_INTERVAL = 100;

    private Mockery context = new Mockery();

    private Node localNode;

    @Before
    public void setup() throws Exception {
        localNode = new JGroupsNode(new Identity(8080), new IpAddress());
    }
    
    @Test
    public void ensureHealthMonitorBroadcastHeartbeatPeriodically() throws Exception {
        final Space space = context.mock(Space.class);

        mockGetLocalNodeOn(space);
        mockHealthCheckOn(space);
        mockMultipleBroadcastOn(space);

        HealthMonitor monitor = new HealthMonitor(space, TEST_INTERVAL,
                TimeUnit.MILLISECONDS);

        monitor.start();

        Thread.sleep(TEST_INTERVAL * 3);

        context.assertIsSatisfied();
    }
    
    private void mockHealthCheckOn(final Space space) throws IOException {
        context.checking(new Expectations() {
            {
                atLeast(1).of(space).isActive();
                will(returnValue(true));
            }
        });
    }
    
    private void mockMultipleBroadcastOn(final Space space) throws IOException {
        context.checking(new Expectations() {
            {
                atLeast(2).of(space).broadcast(with(any(HeartBeat.class)));
            }
        });
    }
    
    @Test
    public void ensureHealthMonitorCanBeStoppedGracefully() throws Exception{
        final Space space = context.mock(Space.class);
        
        mockGetLocalNodeOn(space);
        mockHealthCheckOn(space);
        mockBroadcastOn(space);
        
        HealthMonitor monitor = new HealthMonitor(space, TEST_INTERVAL,
                TimeUnit.MILLISECONDS);
        
        assertFalse("Mintor should not be running", monitor.isRunning());

        monitor.start();
        
        assertTrue("Mintor should be running", monitor.isRunning());
        
        monitor.stop();
        
        Thread.sleep(TEST_INTERVAL * 2);
        
        assertFalse("Mintor should not be running", monitor.isRunning());
    }

    @Test
    public void ensureHealthMonitorBroadcastHeartbeat() throws Exception {
        final Space space = context.mock(Space.class);

        mockGetLocalNodeOn(space);
        mockHealthCheckOn(space);
        mockBroadcastOn(space);

        HealthMonitor monitor = new HealthMonitor(space, TEST_INTERVAL,
                TimeUnit.MILLISECONDS);

        monitor.start();

        Thread.sleep(TEST_INTERVAL * 2);

        context.assertIsSatisfied();
    }

    private void mockGetLocalNodeOn(final Space space) throws IOException {
        context.checking(new Expectations() {
            {
                atLeast(1).of(space).getLocalNode();
                will(returnValue(localNode));
            }
        });
    }

    private void mockBroadcastOn(final Space space) throws IOException {
        context.checking(new Expectations() {
            {
                atLeast(1).of(space).broadcast(with(any(HeartBeat.class)));
            }
        });
    }

}
