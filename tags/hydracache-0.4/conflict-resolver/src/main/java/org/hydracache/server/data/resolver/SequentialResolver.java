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
import java.util.Iterator;

import org.apache.commons.lang.Validate;
import org.hydracache.server.data.versioning.Versioned;

/**
 * Resolves conflict by calling a sequence of <code>ConflictResolver</code> in
 * the order it was configured with and by stopping as soon as no more conflict
 * exists or by return the result of the last resolver.
 * 
 * @author David Dossot (david@dossot.net)
 */
public final class SequentialResolver implements ConflictResolver {

    private final Collection<? extends ConflictResolver> resolvers;

    public SequentialResolver(final Collection<ConflictResolver> resolvers) {
        Validate
                .notEmpty(resolvers, "resolvers must be non-null and non-empty");

        this.resolvers = Collections.unmodifiableCollection(resolvers);
    }

    public ResolutionResult resolve(final Collection<? extends Versioned> conflict) {
        for (final Iterator<? extends ConflictResolver> i = resolvers.iterator(); i
                .hasNext();) {

            final ConflictResolver resolver = i.next();

            if (!i.hasNext()) {
                return resolver.resolve(conflict);
            }

            final ResolutionResult result = resolver.resolve(conflict);

            if (!result.stillHasConflict()) {
                return result;
            }
        }

        throw new IllegalStateException("This should never be reached");
    }
}
