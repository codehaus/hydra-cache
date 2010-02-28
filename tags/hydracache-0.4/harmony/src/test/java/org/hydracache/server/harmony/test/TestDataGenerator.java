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
package org.hydracache.server.harmony.test;

import java.net.UnknownHostException;

import org.apache.commons.lang.RandomStringUtils;
import org.hydracache.server.Identity;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.data.versioning.IncrementVersionFactory;

/**
 * @author nzhu
 * 
 */
public final class TestDataGenerator {

    private static final int RANDOM_DATA_LENGTH = 10;

    private static Identity DEFAULT_NODE_ID;

    private TestDataGenerator() throws UnknownHostException {
        super();
        DEFAULT_NODE_ID = new Identity(1);
    }

    public static Data createRandomData() {
        final IncrementVersionFactory versionFactory =
                new IncrementVersionFactory();
        final Data data =
                new Data(
                        1L,
                        versionFactory.create(DEFAULT_NODE_ID),
                        RandomStringUtils.randomAlphanumeric(RANDOM_DATA_LENGTH).getBytes());
        return data;
    }

}
