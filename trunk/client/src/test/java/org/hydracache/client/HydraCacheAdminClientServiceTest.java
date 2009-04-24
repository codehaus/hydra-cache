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
package org.hydracache.client;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.hydracache.client.partition.PartitionAwareClient;
import org.hydracache.client.transport.Transport;
import org.hydracache.data.hashing.KetamaBasedHashFunction;
import org.hydracache.server.Identity;
import org.hydracache.server.harmony.core.SubstancePartition;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * @author Tan Quach
 * @since 1.0
 */
public class HydraCacheAdminClientServiceTest {
    private HydraCacheAdminClient service;
    private Mockery context;
    private Transport transport;

    @Before
    public void beforeTestMethods() throws Exception {
        this.transport = this.context.mock(Transport.class);
        this.service = new PartitionAwareClient(new SubstancePartition(new KetamaBasedHashFunction(), Collections.emptyList()));
    }
    
    @Test
    public void shouldReturnEmptyListWhenCurrentPartitionIsNull() throws Exception {
        List<Identity> nodes = this.service.listNodes();
        assertNotNull(nodes);
        assertTrue(nodes.isEmpty());
    }
}
