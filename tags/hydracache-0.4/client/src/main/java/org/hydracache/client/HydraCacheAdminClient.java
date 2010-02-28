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
package org.hydracache.client;

import java.util.List;

import org.hydracache.server.Identity;

/**
 * Manages administration calls to the server.
 * 
 * @author Tan Quach
 * @since 1.0
 */
public interface HydraCacheAdminClient {

    /**
     * Retrieve a list of all known nodes in the space.
     * 
     * @return a list of Identity objects identifying all known nodes in the cache.
     * @throws Exception 
     */
    List<Identity> listNodes() throws Exception;
}
