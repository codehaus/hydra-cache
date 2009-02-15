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

import org.apache.commons.lang.Validate;
import org.hydracache.server.data.versioning.Versioned;

/**
 * Resolves conflict by removing all versions that are equal and keeping only
 * descendants.
 * 
 * @author David Dossot (david@dossot.net)
 */
public final class SyntacticReconciliationResolver implements ConflictResolver {

    public ResolutionResult resolve(final Collection<Versioned> conflict) {
        Validate.notEmpty(conflict,
                "conflict must be a non-null not-empty collection");

        if (conflict.size() == 1) {
            return new DefaultResolutionResult(conflict,
                    DefaultResolutionResult.EMPTY_VERSIONED_COLLECTION);
        }

        final List<Versioned> alive = new ArrayList<Versioned>();
        final List<Versioned> expired = new ArrayList<Versioned>();

        sortAliveFromExpired(conflict, alive, expired);

        return new DefaultResolutionResult(alive, expired);
    }

    private static void sortAliveFromExpired(
            final Collection<Versioned> conflict, final List<Versioned> alive,
            final List<Versioned> expired) {

        final List<Versioned> sortedConflict = new ArrayList<Versioned>(
                conflict);

        Collections.sort(sortedConflict, VersionComparator.VERSION_COMPARATOR);
        final int sizeOfConflict = sortedConflict.size();

        for (int i = 0; i < sizeOfConflict; i++) {

            final Versioned currentVersioned = sortedConflict.get(i);

            if (i < sizeOfConflict - 1) {

                final Versioned nextVersioned = sortedConflict.get(i + 1);

                if (isVersionInConflict(currentVersioned, nextVersioned)) {
                    alive.add(currentVersioned);
                    alive.add(nextVersioned);
                    i++;
                } else {
                    expired.add(currentVersioned);
                }

            } else {
                alive.add(currentVersioned);
            }
        }
    }

    private static boolean isVersionInConflict(final Versioned o1,
            final Versioned o2) {

        return !o1.getVersion().isDescendantOf(o2.getVersion())
                && !o2.getVersion().isDescendantOf(o1.getVersion());
    }
}
