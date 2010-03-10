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

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetSocketAddress;

import org.apache.http.nio.reactor.IOEventDispatch;
import org.apache.http.nio.reactor.IOReactorStatus;
import org.apache.http.nio.reactor.ListeningIOReactor;
import org.apache.log4j.Logger;
import org.hydracache.server.Identity;
import org.springframework.context.Lifecycle;

/**
 * An ultra-light and non-blocking async HTTP server built for serving
 * client-server
 * 
 * @author nzhu
 * 
 */
public final class AsyncHttpLightServer implements Lifecycle {

    private static Logger log = Logger.getLogger(AsyncHttpLightServer.class);

    private IOEventDispatch ioEventDispatch;

    private ListeningIOReactor ioReactor;

    private int portNumber;

    private String ip;

    private Identity id;

    public AsyncHttpLightServer(Identity id, ListeningIOReactor ioReactor,
            IOEventDispatch ioEventDispatch, String ip, int portNumber) {
        this.id = id;
        this.ioEventDispatch = ioEventDispatch;
        this.ioReactor = ioReactor;
        this.portNumber = portNumber;
        this.ip = ip;
    }

    public boolean isInitialized() {
        return ioReactor != null && ioEventDispatch != null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public boolean isRunning() {
        return IOReactorStatus.ACTIVE == ioReactor.getStatus();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    public void start() {
        if (!isInitialized())
            throw new IllegalStateException(
                    "Server initialization is incomplete, can not start the server");

        log.info("Starting Http Server [" + id + "] ... ");

        try {
            ioReactor.listen(buildInetSocketAddress());

            log.info("Http Server started");

            ioReactor.execute(ioEventDispatch);
        } catch (InterruptedIOException iex) {
            log.error("Server was interrupted: ", iex);
        } catch (IOException ioex) {
            log.error("IO exception: ", ioex);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public void stop() {
        log.info("Stopping Http Server [" + id + "] ... ");

        try {
            ioReactor.shutdown();
        } catch (IOException e) {
            log.warn("Error occured while shutting down", e);
        }

        log.info("Http Server stopped");
    }

    InetSocketAddress buildInetSocketAddress() {
        InetSocketAddress address = null;

        address = new InetSocketAddress(ip, portNumber);

        if (log.isDebugEnabled()) {
            log.debug("Server is listening on address ["
                    + address.getAddress().getHostAddress() + ":"
                    + address.getPort() + "]");
        }

        return address;
    }
}
