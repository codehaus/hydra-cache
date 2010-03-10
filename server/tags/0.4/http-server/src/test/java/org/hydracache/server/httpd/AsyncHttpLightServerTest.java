/*
 * Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hydracache.server.httpd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.apache.http.nio.reactor.IOEventDispatch;
import org.apache.http.nio.reactor.IOReactorStatus;
import org.apache.http.nio.reactor.ListeningIOReactor;
import org.hydracache.server.Identity;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

/**
 * @author nzhu
 * 
 */
public class AsyncHttpLightServerTest {

    private static final String TEST_IP = "127.0.0.1";

    private static final int TEST_PORT = 8080;

    private Identity id;

    private Mockery context = new Mockery();

    @Before
    public void setup() throws Exception {
        id = new Identity(Inet4Address.getLocalHost(), 80);
    }

    @Test(expected = IllegalStateException.class)
    public void testIncompleteInitShouldCauseException() {
        AsyncHttpLightServer server = new AsyncHttpLightServer(id, null, null,
                TEST_IP, TEST_PORT);

        assertFalse(server.isInitialized());

        server.start();
    }

    @Test
    public void testBuildInetAddressWithIp() {
        AsyncHttpLightServer server = new AsyncHttpLightServer(id, null, null,
                TEST_IP, TEST_PORT);

        InetSocketAddress address = server.buildInetSocketAddress();

        assertFalse(address.isUnresolved());
        assertFalse(address.getAddress().isAnyLocalAddress());
        assertEquals(TEST_IP, address.getAddress().getHostAddress());
    }

    @Test
    public void testServerLifeCycle() throws Exception {

        IOEventDispatch eventDispatch = context.mock(IOEventDispatch.class);

        ListeningIOReactor ioReactor = context.mock(ListeningIOReactor.class);

        {
            addStartListeningExpectation(eventDispatch, ioReactor);

            addReturnActiveStatusExpectation(ioReactor);

            addShutdownExpectation(ioReactor);

            addReturnShutdownStatusExpectation(ioReactor);
        }

        AsyncHttpLightServer server = new AsyncHttpLightServer(id, ioReactor,
                eventDispatch, TEST_IP, 8080);

        assertTrue(server.isInitialized());

        server.start();

        assertTrue(server.isRunning());

        server.stop();

        assertFalse(server.isRunning());
    }

    private void addReturnShutdownStatusExpectation(
            final ListeningIOReactor ioReactor) {
        context.checking(new Expectations() {
            {
                one(ioReactor).getStatus();
                will(returnValue(IOReactorStatus.SHUT_DOWN));
            }
        });
    }

    private void addShutdownExpectation(final ListeningIOReactor ioReactor)
            throws IOException {
        context.checking(new Expectations() {
            {
                one(ioReactor).shutdown();
            }
        });
    }

    private void addReturnActiveStatusExpectation(
            final ListeningIOReactor ioReactor) {
        context.checking(new Expectations() {
            {
                one(ioReactor).getStatus();
                will(returnValue(IOReactorStatus.ACTIVE));
            }
        });
    }

    private void addStartListeningExpectation(
            final IOEventDispatch eventDispatch,
            final ListeningIOReactor ioReactor) throws IOException {
        context.checking(new Expectations() {
            {
                one(ioReactor).listen(with(any(SocketAddress.class)));
                one(ioReactor).execute(eventDispatch);
            }
        });
    }
}
