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

import org.hydracache.io.Marshaller;
import org.hydracache.server.data.resolver.ConflictResolver;
import org.hydracache.server.data.versioning.VersionFactory;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author David Dossot (david@dossot.net)
 */
public class SpringConfigurationTest {

    @Test
    public void expectedBeanTypes() {
        final BeanFactory factory = new ClassPathXmlApplicationContext(
                new String[] { "conflict-resolver-context.xml",
                        "test-context.xml" });

        factory.isTypeMatch("versionFactory", VersionFactory.class);
        factory.isTypeMatch("versionMarshaller", Marshaller.class);
        factory.isTypeMatch("conflictResolver", ConflictResolver.class);
    }
}
