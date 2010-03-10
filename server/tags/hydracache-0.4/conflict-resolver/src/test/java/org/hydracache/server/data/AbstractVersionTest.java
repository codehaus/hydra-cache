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
package org.hydracache.server.data;

import java.net.UnknownHostException;

import org.hydracache.server.Identity;
import org.junit.BeforeClass;

/**
 * @author David Dossot (david@dossot.net)
 */
public abstract class AbstractVersionTest {

    protected static Identity A;
    protected static Identity B;
    protected static Identity C;

    @BeforeClass
    public static void initializeTestIdentities() throws UnknownHostException {

        A = new Identity(1);
        B = new Identity(2);
        C = new Identity(3);
    }

}
