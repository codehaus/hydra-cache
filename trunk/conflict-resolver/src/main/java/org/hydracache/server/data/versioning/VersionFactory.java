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
package org.hydracache.server.data.versioning;

import org.hydracache.io.Marshaller;
import org.hydracache.server.Identity;

/**
 * Defines the factory in charge of creating, marshalling and unmarshalling
 * Version objects.
 * 
 * Note that Strings in Version objects are represented in <a href=
 * "http://java.sun.com/javase/6/docs/api/java/io/DataInput.html#modified-utf-8"
 * >modified UTF-8</a>.
 * 
 * @author David Dossot (david@dossot.net)
 * 
 * @see Version
 */
public interface VersionFactory extends Marshaller<Version> {
    
    /**
     * Create a null version instance
     */
    Version createNull();
    
    /**
     * Creates a new version object for the provided node ID.
     */
    Version create(Identity nodeId);
}
