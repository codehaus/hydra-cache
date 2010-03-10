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

import org.hydracache.server.data.versioning.Versioned;

/**
 * @author David Dossot (david@dossot.net)
 */
public interface ResolutionResult {

    /**
     * @return true if the resolver was not able to clear the conflict.
     */
    public abstract boolean stillHasConflict();

    /**
     * @return a collection of <code>Versioned</code> object that are
     *         superseded by the because they are replaced with more recent
     *         versions.
     */
    public abstract Collection<? extends Versioned> getExpired();

    /**
     * @return a collection of alive <code>Versioned</code> objects after the
     *         resolution has been done.
     */
    public abstract Collection<? extends Versioned> getAlive();

}