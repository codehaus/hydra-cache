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

import org.apache.http.HttpException;
import org.apache.http.nio.NHttpConnection;
import org.apache.http.nio.protocol.EventListener;
import org.apache.log4j.Logger;

/**
 * Http protocol event listener
 * 
 * @author nzhu
 * 
 */
public class SimpleProtocolEventListener implements EventListener {
    static Logger log = Logger
            .getLogger(SimpleProtocolEventListener.class);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.http.nio.protocol.EventListener#connectionClosed(org.apache
     * .http.nio.NHttpConnection)
     */
    @Override
    public void connectionClosed(NHttpConnection conn) {
        if (log.isDebugEnabled())
            log.debug("Closed connection: " + conn);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.http.nio.protocol.EventListener#connectionOpen(org.apache.
     * http.nio.NHttpConnection)
     */
    @Override
    public void connectionOpen(NHttpConnection conn) {
        if (log.isDebugEnabled())
            log.debug("Open connection: " + conn);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.http.nio.protocol.EventListener#connectionTimeout(org.apache
     * .http.nio.NHttpConnection)
     */
    @Override
    public void connectionTimeout(NHttpConnection conn) {
        if (log.isDebugEnabled())
            log.debug("Timed out connection: " + conn);
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.apache.http.nio.protocol.EventListener#fatalIOException(java.io.
     * IOException, org.apache.http.nio.NHttpConnection)
     */
    @Override
    public void fatalIOException(IOException ex, NHttpConnection conn) {
        /*
         * TODO: Ugly exception message based checking, but I can't seem to find
         * a better way handling this situation. Similar strategy is employed in
         * Apache Axis2 implementation. Hopefully we can find a better way to
         * handle this properly.
         */
        if (ex.getMessage().indexOf("Connection reset") != -1
                || ex.getMessage().indexOf("forcibly closed") != -1) {
            if (log.isDebugEnabled())
                log.debug("HTTP connection " + conn + ": " + ex.getMessage());
        } else {
            log.error("Fatal IO error occured on connection: " + conn, ex);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.http.nio.protocol.EventListener#fatalProtocolException(org
     * .apache.http.HttpException, org.apache.http.nio.NHttpConnection)
     */
    @Override
    public void fatalProtocolException(HttpException ex, NHttpConnection conn) {
        log.error("Fatal protocol error occured on connection: " + conn, ex);
    }

}
