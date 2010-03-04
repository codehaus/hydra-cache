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
package org.hydracache.server.data.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.UnknownHostException;
import java.util.Collection;

import net.sf.ehcache.CacheManager;

import org.hydracache.server.Identity;
import org.hydracache.server.data.resolver.SyntacticReconciliationResolver;
import org.hydracache.server.data.versioning.IncrementVersionFactory;
import org.hydracache.server.data.versioning.Version;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author nzhu
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/data-bank-test-context.xml" })
public class EhcacheDataBankTest {

    private static final long DEFAULT_KEY_HASH = 1234L;

    private static final long NON_EXISTANT_KEY_HASH = DEFAULT_KEY_HASH + 1;

    private Identity NODE_A;

    private Identity NODE_B;

    private CacheManager cacheManager;
    
    private String context = "testContext";

    @Before
    public void before() throws UnknownHostException {
        cacheManager = CacheManager.create();
        NODE_A = new Identity(1);
        NODE_B = new Identity(2);
    }

    @After
    public void after() {
        cacheManager.clearAll();
    }
    
    @Test
    public void testPutAndGetWithDefaultContext() throws Exception {
        final DataBank dataBank = createDataBank();

        final Data data = generateData(NODE_A);
        
        dataBank.put("", data);

        final Data data2 = dataBank.get(DataBank.DEFAULT_CACHE_CONTEXT_NAME, data.getKeyHash());

        assertEquals("Straight get after put retrieved incorrect data", data,
                data2);
    }
    
    @Test
    public void testDeletion() throws Exception {
        final DataBank dataBank = createDataBank();

        final Data data = generateData(NODE_A);
        
        dataBank.put(context, data);

        final Data data2 = dataBank.get(context, data.getKeyHash());

        assertEquals("Straight get after put retrieved incorrect data", data,
                data2);
        
        dataBank.delete(context, data.getKeyHash());
        
        assertEquals("Data should be removed", null, dataBank.get(context, data.getKeyHash()));
    }
    
    @Test
    public void testDeletionWithNonExistData() throws Exception {
        final DataBank dataBank = createDataBank();

        dataBank.delete(context, 1L);
        
        assertEquals("Data should be removed", null, dataBank.get(context, 1L));
    }

    @Test
    public void testPutAndGetWithStorage() throws Exception {
        final DataBank dataBank = createDataBank();

        final Data data = generateData(NODE_A);
        
        dataBank.put(context, data);

        final Data data2 = dataBank.get(context, data.getKeyHash());

        assertEquals("Straight get after put retrieved incorrect data", data,
                data2);
    }

    @Test
    public void testPutDataWithNewVersion() throws Exception {
        final DataBank dataBank = createDataBank();

        Data data = generateData(NODE_A);

        dataBank.put(context, data);

        final Data newData = generateData(NODE_A);

        newData.setVersion(newData.getVersion().incrementFor(NODE_A));

        dataBank.put(context, newData);

        data = dataBank.get(context, DEFAULT_KEY_HASH);

        assertEquals(
                "Put and get with sequential version increment returned incorrect result",
                newData, data);
    }

    @Test
    public void testGetNonexistantData() throws Exception {
        final DataBank dataBank = createDataBank();

        final Data data = dataBank.get(context, NON_EXISTANT_KEY_HASH);

        assertTrue("Null should be returned", data == null);
    }

    @Test(expected = DataConflictException.class)
    public void testPutWithIncompatibleVersion() throws Exception {
        final DataBank dataBank = createDataBank();

        final Data data = generateData(NODE_A);

        dataBank.put(context, data);

        final Data data2 = generateData(NODE_B);

        dataBank.put(context, data2);
    }

    @Test
    public void ensureCanGetAllData() throws Exception {
        final DataBank dataBank = createDataBank();

        Data data = generateData(NODE_A);
        dataBank.put(context, data);

        Collection<Data> allData = dataBank.getAll();

        assertEquals("Data result size is incorrect", 1, allData.size());
    }

    private DataBank createDataBank() {
        final DataBank dataBank = new EhcacheDataBank(
                new SyntacticReconciliationResolver(), cacheManager);
        return dataBank;
    }

    private Data generateData(final Identity nodeId) {
        final Version version = new IncrementVersionFactory().create(nodeId);

        final Long dataKey = DEFAULT_KEY_HASH;

        final Data data = new Data(dataKey, version, new byte[10]);

        return data;
    }

}
