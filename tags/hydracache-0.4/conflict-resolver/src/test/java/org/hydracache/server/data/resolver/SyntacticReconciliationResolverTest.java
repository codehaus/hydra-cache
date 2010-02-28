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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.hydracache.server.data.versioning.Versioned;

/**
 * @author David Dossot (david@dossot.net)
 */
public class SyntacticReconciliationResolverTest extends AbstractResolverTest {

    @Override
    protected ConflictResolver newConflictResolver() {
        return new SyntacticReconciliationResolver();
    }

    @Override
    protected void oneEntry(final ResolutionResult result) {
        assertFalse("stillHasConflict", result.stillHasConflict());
        assertEquals(1, result.getAlive().size());
        assertEquals(0, result.getExpired().size());
    }

    @Override
    protected void sameVersions(final ResolutionResult result) {
        assertTrue("stillHasConflict", result.stillHasConflict());
        assertEquals(2, result.getAlive().size());
        assertEquals(0, result.getExpired().size());
    }

    @Override
    protected void nonDescendant(final ResolutionResult result) {
        sameVersions(result);
    }

    @Override
    protected void oneDescendant(final Versioned vs1,
            final Versioned vs2, final ResolutionResult result) {

        assertFalse("stillHasConflict", result.stillHasConflict());

        assertEquals(1, result.getAlive().size());
        assertEquals(vs2, result.getAlive().iterator().next());

        assertEquals(1, result.getExpired().size());
        assertEquals(vs1, result.getExpired().iterator().next());
    }

    @Override
    protected void twoDescendant(final Versioned vs1,
            final Versioned vs2, final Versioned vs3,
            final ResolutionResult result) {

        assertFalse("stillHasConflict", result.stillHasConflict());

        assertEquals(1, result.getAlive().size());
        assertEquals(vs3, result.getAlive().iterator().next());

        assertEquals(2, result.getExpired().size());
        assertTrue(result.getExpired().contains(vs1));
        assertTrue(result.getExpired().contains(vs2));
    }

    @Override
    protected void mixedFirst(final Versioned vs1,
            final Versioned vs2, final Versioned vs3,
            final ResolutionResult result) {
        assertTrue("stillHasConflict", result.stillHasConflict());

        assertEquals(3, result.getAlive().size());
        assertTrue(result.getAlive().contains(vs1));
        assertTrue(result.getAlive().contains(vs2));
        assertTrue(result.getAlive().contains(vs3));

        assertEquals(0, result.getExpired().size());
    }

    @Override
    protected void mixedLast(final Versioned vs1, final Versioned vs2,
            final Versioned vs3, final ResolutionResult result) {

        assertTrue("stillHasConflict", result.stillHasConflict());

        assertEquals(2, result.getAlive().size());
        assertTrue(result.getAlive().contains(vs2));
        assertTrue(result.getAlive().contains(vs3));

        assertEquals(1, result.getExpired().size());
        assertEquals(vs1, result.getExpired().iterator().next());
    }
}
