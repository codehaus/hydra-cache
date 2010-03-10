package org.hydracache.server.bootstrap;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import static org.junit.Assert.*;

public class SpringContainerTest {
    private Mockery context = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    @Test
    public void testStart() {
        final Runtime mockRuntime = context.mock(Runtime.class);

        context.checking(new Expectations() {
            {
                one(mockRuntime).addShutdownHook(with(any(Thread.class)));
            }
        });

        Container container = new SpringContainer(mockRuntime,
                new String[] { "test-context.xml" });

        container.start();

        assertTrue("Container should be started", container.isRunning());

        container.stop();

        assertFalse("Container should be stopped", container.isRunning());

        context.assertIsSatisfied();
    }
}
