/*
 * Copyright 2010 the original author or authors.
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
package org.hydracache.client.partition;

import java.util.Arrays;
import java.util.List;

import org.hydracache.server.Identity;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Nick Zhu (nzhu@jointsource.com)
 * 
 */
public class ObservableRegistryTest {

    @Test
    public void ensureRegistryCanDetectNodeAdditionChange() {
        ObservableRegistry registry = new ObservableRegistry(Arrays.asList(
                new Identity(80), new Identity(81)));

        assertFalse("Newly created registry should not be changed", registry
                .hasChanged());

        registry.update(Arrays.asList(new Identity(80), new Identity(81),
                new Identity(82)));

        assertTrue("Registry should be changed", registry.hasChanged());
    }

    @Test
    public void ensureRegistryCanDetectNodeRemoveChange() {
        ObservableRegistry registry = new ObservableRegistry(Arrays.asList(
                new Identity(80), new Identity(81)));

        registry.update(Arrays.asList(new Identity(80)));

        assertTrue("Registry should be changed", registry.hasChanged());
    }

    @Test
    public void ensureRegistryUpdatesWithChange() {
        ObservableRegistry registry = new ObservableRegistry(Arrays.asList(
                new Identity(80), new Identity(81)));

        registry.update(Arrays.asList(new Identity(80)));

        registry.update(Arrays.asList(new Identity(80)));

        assertFalse("Registry should be changed", registry.hasChanged());
    }

    @Test
    public void ensureRegistryCanDetectNodeChange() {
        ObservableRegistry registry = new ObservableRegistry(Arrays.asList(
                new Identity(80), new Identity(81)));

        registry.update(Arrays.asList(new Identity(80), new Identity(82)));

        assertTrue("Registry should be changed", registry.hasChanged());
    }

    @Test
    public void ensureRegistryCanDetectNoChange() {
        ObservableRegistry registry = new ObservableRegistry(Arrays.asList(
                new Identity(80), new Identity(81)));

        registry.update(Arrays.asList(new Identity(80), new Identity(81)));

        assertFalse("Registry should not be changed", registry.hasChanged());
    }

    @Test
    public void ensureRegistryCanHandleNull() {
        ObservableRegistry registry = new ObservableRegistry(null);

        registry.update(Arrays.asList(new Identity(80), new Identity(81)));

        assertTrue("Registry should not be changed", registry.hasChanged());
    }

    @Test
    public void ensureRegistryCanHandleNullInUpdate() {
        ObservableRegistry registry = new ObservableRegistry(Arrays.asList(
                new Identity(80), new Identity(81)));

        registry.update(null);

        assertFalse("Registry should not be changed", registry.hasChanged());
    }

    @Test
    public void ensureRegistryDiscardNullInUpdate() {
        List<Identity> originalList = Arrays.asList(new Identity(80),
                new Identity(81));
        
        ObservableRegistry registry = new ObservableRegistry(originalList);

        registry.update(null);

        registry.update(originalList);

        assertFalse("Registry should not be changed", registry.hasChanged());
    }

}
