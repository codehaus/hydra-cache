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

import java.io.Serializable;
import java.util.Comparator;

import org.hydracache.server.data.versioning.Version;
import org.hydracache.server.data.versioning.Versioned;

/**
 * @author David Dossot (david@dossot.net)
 */
final class VersionComparator implements Comparator<Versioned>, Serializable {

    private static final long serialVersionUID = 3619639751971825726L;

    static final VersionComparator VERSION_COMPARATOR = new VersionComparator();

    private VersionComparator() {
        // NOOP
    }

    @Override
    public int compare(final Versioned o1, final Versioned o2) {
        final Version v1 = o1.getVersion();
        final Version v2 = o2.getVersion();
        return v1.isDescendantOf(v2) ? 1 : v2.isDescendantOf(v1) ? -1 : 0;
    }

}