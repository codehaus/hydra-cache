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
package org.hydracache.server.data.versioning;

/**
 * The context encompasses a set of versions (or versioned objects - TBD) and is
 * returned at get time with the list of fetches objects. It allows the client
 * to realize there is a read conflict and, if it can resolve it, it allows it
 * to flag the issue as resolved. It then used at put time to indicate that the
 * data being written back has resolved any particular conflict, allowing the
 * server to get rid of them.
 * 
 * @author David Dossot (david@dossot.net)
 */
public interface Context {

    boolean hasConflict();

    void markResolved();

}
