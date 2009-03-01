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

import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.hydracache.server.data.AbstractVersionTest;
import org.hydracache.server.data.versioning.IncrementVersionFactory;
import org.hydracache.server.data.versioning.Versioned;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author David Dossot (david@dossot.net)
 */
@RunWith(JMock.class)
public class SequentialResolverTest extends AbstractVersionTest {

    private static Collection<Versioned> FAKE_CONFLICT;

    Mockery context = new JUnit4Mockery();

    @BeforeClass
    public static void initialize() {
        FAKE_CONFLICT = Collections.singleton((Versioned) new VersionedStub(
                new IncrementVersionFactory().create(A)));
    }

    @Test
    public void singleResolverWorks() {

        final ConflictResolver singleResolver = context
                .mock(ConflictResolver.class);

        final ResolutionResult result = context.mock(ResolutionResult.class);

        context.checking(new Expectations() {
            {
                oneOf(singleResolver).resolve(FAKE_CONFLICT);
                will(returnValue(result));

                never(result);
            }
        });

        final SequentialResolver resolver = new SequentialResolver(Collections
                .singleton(singleResolver));

        assertNotNull(resolver.resolve(FAKE_CONFLICT));
    }

    @Test
    public void stopWhenConflictResolved() {

        final ConflictResolver succesfulResolver = context.mock(
                ConflictResolver.class, "succesfulResolver");

        final ResolutionResult resolvedResult = context
                .mock(ResolutionResult.class);

        final ConflictResolver ignoredResolver = context.mock(
                ConflictResolver.class, "ignoredResolver");

        context.checking(new Expectations() {
            {
                oneOf(succesfulResolver).resolve(FAKE_CONFLICT);
                will(returnValue(resolvedResult));

                oneOf(resolvedResult).stillHasConflict();
                will(returnValue(false));

                never(ignoredResolver);
            }
        });

        final SequentialResolver resolver = new SequentialResolver(Arrays
                .asList(succesfulResolver, ignoredResolver));

        assertNotNull(resolver.resolve(FAKE_CONFLICT));
    }

    @Test
    public void lastIfNeverResolved() {

        final ConflictResolver firstFailedResolver = context.mock(
                ConflictResolver.class, "firstFailedResolver");

        final ResolutionResult firstFailedResult = context.mock(
                ResolutionResult.class, "firstFailedResult");

        final ConflictResolver secondResolver = context.mock(
                ConflictResolver.class, "secondResolver");

        final ResolutionResult secondResult = context.mock(
                ResolutionResult.class, "secondResult");

        context.checking(new Expectations() {
            {
                oneOf(firstFailedResolver).resolve(FAKE_CONFLICT);
                will(returnValue(firstFailedResult));

                oneOf(firstFailedResult).stillHasConflict();
                will(returnValue(true));

                oneOf(secondResolver).resolve(FAKE_CONFLICT);
                will(returnValue(secondResult));

                never(secondResult);
            }
        });

        final SequentialResolver resolver = new SequentialResolver(Arrays
                .asList(firstFailedResolver, secondResolver));

        assertNotNull(resolver.resolve(FAKE_CONFLICT));
    }
}
