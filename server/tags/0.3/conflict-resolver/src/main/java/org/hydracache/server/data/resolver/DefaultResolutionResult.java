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

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang.Validate;
import org.hydracache.server.data.versioning.Versioned;

/**
 * @author David Dossot (david@dossot.net)
 */
final class DefaultResolutionResult implements ResolutionResult {

    static final Collection<Versioned> EMPTY_VERSIONED_COLLECTION = Collections
            .emptyList();

    private final boolean stillHasConflict;
    private final Collection<Versioned> alive;
    private final Collection<Versioned> expired;

    public DefaultResolutionResult(final Collection<Versioned> alive,
            final Collection<Versioned> expired) {

        Validate.notEmpty(alive, "alive can not be empty");
        Validate.notNull(expired, "expired can not be null");

        this.alive = Collections.unmodifiableCollection(alive);
        this.expired = Collections.unmodifiableCollection(expired);
        this.stillHasConflict = this.alive.size() > 1;
    }

    public boolean stillHasConflict() {
        return stillHasConflict;
    }

    public Collection<Versioned> getExpired() {
        return expired;
    }

    public Collection<Versioned> getAlive() {
        return alive;
    }
}
