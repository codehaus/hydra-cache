package org.hydracache.server.httpd;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.log4j.Logger;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

public class SimpleProtocolEventListenerTest {
    private Mockery context;

    private SimpleProtocolEventListener listener;

    @Before
    public void setup() {
        context = new Mockery() {
            {
                setImposteriser(ClassImposteriser.INSTANCE);
            }
        };

        listener = new SimpleProtocolEventListener();
    }

    @Test
    public void ensureConnectionResetExceptionIsIgnored() {
        setLoggerForDebugOutput();

        listener.fatalIOException(new IOException(
                "Connection reset blah blah blah"), null);

        context.assertIsSatisfied();
    }

    @Test
    public void ensureConnectionClosedExceptionIsIgnored() {
        setLoggerForDebugOutput();

        listener.fatalIOException(new IOException(
                "Connection forcibly closed blah blah blah"), null);

        context.assertIsSatisfied();
    }

    private void setLoggerForDebugOutput() {
        final Logger log = context.mock(Logger.class);

        addDebugExpectation(log);

        SimpleProtocolEventListener.log = log;
    }

    private void addDebugExpectation(final Logger log) {
        context.checking(new Expectations() {
            {
                one(log).isDebugEnabled();
                will(returnValue(true));
                one(log).debug(with(any(String.class)));
            }
        });
    }

    @Test
    public void ensureOtherIOExceptionIsNotIgnored() {
        setLoggerForErrorOutput();

        listener.fatalIOException(new IOException(
                "Connection IO blah blah blah"), null);

        context.assertIsSatisfied();
    }

    private void setLoggerForErrorOutput() {
        final Logger log = context.mock(Logger.class);

        addErrorExpectation(log);

        SimpleProtocolEventListener.log = log;
    }

    private void addErrorExpectation(final Logger log) {
        context.checking(new Expectations() {
            {
                one(log).error(with(any(String.class)),
                        with(any(Exception.class)));
            }
        });
    }

    @Test
    public void testConnectionClosed() {
        setLoggerForDebugOutput();
        listener.connectionClosed(null);
    }

    @Test
    public void testConnectionOpen() {
        setLoggerForDebugOutput();
        listener.connectionOpen(null);
    }

    @Test
    public void testConnectionTimeout() {
        setLoggerForDebugOutput();
        listener.connectionTimeout(null);
    }

    @Test
    public void testFatalProtocolException() {
        setLoggerForErrorOutput();
        listener.fatalProtocolException(new HttpException(), null);
    }

}
