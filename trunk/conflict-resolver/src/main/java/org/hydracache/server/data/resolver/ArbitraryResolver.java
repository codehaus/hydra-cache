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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.hydracache.server.data.versioning.Versioned;

/**
 * Resolves conflict by arbitrarily keeping one of the deemed most recent
 * versions in conflict.
 * 
 * @author David Dossot (david@dossot.net)
 */
public final class ArbitraryResolver implements ConflictResolver {

    public ResolutionResult resolve(final Collection<? extends Versioned> conflict) {
        Validate.notEmpty(conflict,
                "conflict must be a non-null not-empty collection");

        if (conflict.size() == 1) {
            return new DefaultResolutionResult(conflict,
                    DefaultResolutionResult.EMPTY_VERSIONED_COLLECTION);
        }

        final List<Versioned> sortedConflict = new ArrayList<Versioned>(
                conflict);

        Collections.sort(sortedConflict, VersionComparator.VERSION_COMPARATOR);

        final List<Versioned> expired = new ArrayList<Versioned>();

        for (int i = 0; i < sortedConflict.size() - 1; i++) {
            expired.add(sortedConflict.get(i));
        }

        final Set<Versioned> alive = Collections.singleton(sortedConflict
                .get(sortedConflict.size() - 1));

        return new DefaultResolutionResult(alive, expired);
    }

}
