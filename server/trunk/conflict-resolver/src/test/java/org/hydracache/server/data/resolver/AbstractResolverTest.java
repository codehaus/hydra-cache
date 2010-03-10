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

import java.util.Arrays;
import java.util.Collections;

import org.hydracache.server.data.AbstractVersionTest;
import org.hydracache.server.data.versioning.IncrementVersionFactory;
import org.hydracache.server.data.versioning.Version;
import org.hydracache.server.data.versioning.VersionFactory;
import org.hydracache.server.data.versioning.Versioned;
import org.junit.Before;
import org.junit.Test;

/**
 * @author David Dossot (david@dossot.net)
 */
public abstract class AbstractResolverTest extends AbstractVersionTest {

    private VersionFactory versionFactory;
    private ConflictResolver resolver;

    @Before
    public void initialize() {
        versionFactory = new IncrementVersionFactory();
        resolver = newConflictResolver();
    }

    protected abstract ConflictResolver newConflictResolver();

    @Test
    public void oneEntry() {
        final Versioned vs = new VersionedStub(versionFactory.create(A));

        final ResolutionResult result = resolver.resolve(Collections
                .singleton(vs));

        oneEntry(result);
    }

    protected abstract void oneEntry(ResolutionResult result);

    @Test
    public void sameVersions() {
        final Version v = versionFactory.create(A);

        final Versioned vs1 = new VersionedStub(v);
        final Versioned vs2 = new VersionedStub(v);

        final ResolutionResult result = resolver.resolve(Arrays
                .asList(vs1, vs2));

        sameVersions(result);
    }

    protected abstract void sameVersions(ResolutionResult result);

    @Test
    public void nonDescendant() {
        final Versioned vs1 = new VersionedStub(versionFactory.create(A)
                .incrementFor(B));

        final Versioned vs2 = new VersionedStub(versionFactory.create(A)
                .incrementFor(C));

        final ResolutionResult result = resolver.resolve(Arrays
                .asList(vs1, vs2));

        nonDescendant(result);
    }

    protected abstract void nonDescendant(ResolutionResult result);

    @Test
    public void oneDescendant() {
        final Version a1 = versionFactory.create(A);
        final Versioned vs1 = new VersionedStub(a1);

        final Version b2 = a1.incrementFor(B);
        final Versioned vs2 = new VersionedStub(b2);

        final ResolutionResult result = resolver.resolve(Arrays
                .asList(vs1, vs2));

        oneDescendant(vs1, vs2, result);
    }

    protected abstract void oneDescendant(Versioned vs1, Versioned vs2,
            ResolutionResult result);

    @Test
    public void twoDescendant() {
        final Version a1 = versionFactory.create(A);
        final Versioned vs1 = new VersionedStub(a1);

        final Version b2 = a1.incrementFor(B);
        final Versioned vs2 = new VersionedStub(b2);

        final Version c3 = b2.incrementFor(C);
        final Versioned vs3 = new VersionedStub(c3);

        final ResolutionResult result = resolver.resolve(Arrays.asList(vs1,
                vs3, vs2));

        twoDescendant(vs1, vs2, vs3, result);
    }

    protected abstract void twoDescendant(Versioned vs1, Versioned vs2,
            Versioned vs3, ResolutionResult result);

    @Test
    public void mixedFirst() {
        final Version a1 = versionFactory.create(A);
        final Versioned vs1 = new VersionedStub(a1);

        final Version b1 = versionFactory.create(B);
        final Versioned vs2 = new VersionedStub(b1);

        final Version c2 = a1.incrementFor(C);
        final Versioned vs3 = new VersionedStub(c2);

        final ResolutionResult result = resolver.resolve(Arrays.asList(vs1,
                vs3, vs2));

        mixedFirst(vs1, vs2, vs3, result);
    }

    protected abstract void mixedFirst(Versioned vs1, Versioned vs2,
            Versioned vs3, ResolutionResult result);

    @Test
    public void mixedLast() {
        final Version a1 = versionFactory.create(A);
        final Versioned vs1 = new VersionedStub(a1);

        final Version b2 = a1.incrementFor(B);
        final Versioned vs2 = new VersionedStub(b2);

        final Version c2 = a1.incrementFor(C);
        final Versioned vs3 = new VersionedStub(c2);

        final ResolutionResult result = resolver.resolve(Arrays.asList(vs1,
                vs3, vs2));

        mixedLast(vs1, vs2, vs3, result);
    }

    protected abstract void mixedLast(Versioned vs1, Versioned vs2,
            Versioned vs3, ResolutionResult result);

}