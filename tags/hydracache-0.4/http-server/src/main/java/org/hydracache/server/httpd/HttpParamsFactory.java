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

import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;

/**
 * Factory class for creating {@link HttpParams} object
 * 
 * @author nzhu
 * 
 */
public class HttpParamsFactory {
    private static Logger log = Logger.getLogger(HttpParamsFactory.class);

    private static final String ORIGIN_SERVER_NAME = "Hydra/1.1";

    private int socketBufferSize = 2 * 1024;

    private int socketTimeout = 3000;

    public void setSocketTimeout(int socketTimeout) {
        if (log.isDebugEnabled()) {
            log.debug("Setting server socket timeout to [" + socketTimeout
                    + "]ms");
        }

        this.socketTimeout = socketTimeout;
    }

    public void setSocketBufferSize(int socketBufferSize) {
        if (log.isDebugEnabled()) {
            log.debug("Setting server socket IO buffer size to ["
                    + socketBufferSize + "]bytes");
        }

        this.socketBufferSize = socketBufferSize;
    }

    public HttpParams create() {
        HttpParams httpParams = new BasicHttpParams();

        httpParams.setIntParameter(CoreConnectionPNames.SO_TIMEOUT,
                socketTimeout);
        httpParams.setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE,
                socketBufferSize);
        httpParams.setBooleanParameter(
                CoreConnectionPNames.STALE_CONNECTION_CHECK, false);
        httpParams.setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true);
        httpParams.setParameter(CoreProtocolPNames.ORIGIN_SERVER,
                ORIGIN_SERVER_NAME);

        return httpParams;
    }

}
