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
package org.hydracache.server.data.resolver;

import org.hydracache.server.data.versioning.Version;
import org.hydracache.server.data.versioning.Versioned;

/**
 * @author David Dossot (david@dossot.net)
 */
class VersionedStub implements Versioned {

    private final Version version;

    public VersionedStub(final Version version) {
        this.version = version;
    }

    @Override
    public Version getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return hashCode() + ":" + getVersion().toString();
    }

}
