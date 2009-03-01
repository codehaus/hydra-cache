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
package org.hydracache.protocol.control.message;

import org.hydracache.server.Identity;
import org.hydracache.server.data.storage.Data;

/**
 * Hydra internal control message that describes help request for a HOP put
 * operation
 * 
 * @author nzhu
 * 
 */
public class PutOperation extends RequestMessage {

    private static final long serialVersionUID = 1L;

    private Data data;

    public PutOperation(Identity source, Data data) {
        super(source);
        
        this.data = data;
    }

    public Data getData() {
        return data;
    }

}
