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
import java.util.Observable;
import java.util.Observer;

import org.hydracache.server.Identity;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Nick Zhu (nzhu@jointsource.com)
 * 
 */
public class ObservableRegistryTest {

    private final class TestObserver implements Observer {
        private boolean changed;

        public void setChanged(boolean changed) {
            this.changed = changed;
        }

        public boolean isChanged() {
            return changed;
        }

        @Override
        public void update(Observable o, Object arg) {
            changed = true;
        }
    }

    @Test
    public void ensureRegistryCanDetectNodeAdditionChange() {
        ObservableRegistry registry = new ObservableRegistry(Arrays.asList(
                new Identity(80), new Identity(81)));

        TestObserver testObserver = new TestObserver();

        registry.addObserver(testObserver);

        assertFalse("Newly created registry should not be changed", registry
                .hasChanged());

        registry.update(Arrays.asList(new Identity(80), new Identity(81),
                new Identity(82)));

        assertTrue("Registry should be changed", testObserver.isChanged());
    }

    @Test
    public void ensureRegistryCanDetectNodeRemoveChange() {
        ObservableRegistry registry = new ObservableRegistry(Arrays.asList(
                new Identity(80), new Identity(81)));

        TestObserver testObserver = new TestObserver();

        registry.addObserver(testObserver);

        registry.update(Arrays.asList(new Identity(80)));

        assertTrue("Registry should be changed", testObserver.isChanged());
    }

    @Test
    public void ensureRegistryUpdatesWithChange() {
        ObservableRegistry registry = new ObservableRegistry(Arrays.asList(
                new Identity(80), new Identity(81)));

        TestObserver testObserver = new TestObserver();

        registry.addObserver(testObserver);

        registry.update(Arrays.asList(new Identity(80)));
        
        testObserver.setChanged(false);

        registry.update(Arrays.asList(new Identity(80)));

        assertFalse("Registry should be changed", testObserver.isChanged());
    }

    @Test
    public void ensureRegistryCanDetectNodeChange() {
        ObservableRegistry registry = new ObservableRegistry(Arrays.asList(
                new Identity(80), new Identity(81)));
        
        TestObserver testObserver = new TestObserver();

        registry.addObserver(testObserver);

        registry.update(Arrays.asList(new Identity(80), new Identity(82)));

        assertTrue("Registry should be changed", testObserver.isChanged());
    }

    @Test
    public void ensureRegistryCanDetectNoChange() {
        ObservableRegistry registry = new ObservableRegistry(Arrays.asList(
                new Identity(80), new Identity(81)));
        
        TestObserver testObserver = new TestObserver();

        registry.addObserver(testObserver);

        registry.update(Arrays.asList(new Identity(80), new Identity(81)));

        assertFalse("Registry should not be changed", testObserver.isChanged());
    }

    @Test
    public void ensureRegistryCanHandleNull() {
        ObservableRegistry registry = new ObservableRegistry(null);
        
        TestObserver testObserver = new TestObserver();

        registry.addObserver(testObserver);

        registry.update(Arrays.asList(new Identity(80), new Identity(81)));

        assertTrue("Registry should not be changed", testObserver.isChanged());
    }

    @Test
    public void ensureRegistryCanHandleNullInUpdate() {
        ObservableRegistry registry = new ObservableRegistry(Arrays.asList(
                new Identity(80), new Identity(81)));
        
        TestObserver testObserver = new TestObserver();

        registry.addObserver(testObserver);

        registry.update(null);

        assertFalse("Registry should not be changed", testObserver.isChanged());
    }

    @Test
    public void ensureRegistryDiscardNullInUpdate() {
        List<Identity> originalList = Arrays.asList(new Identity(80),
                new Identity(81));

        ObservableRegistry registry = new ObservableRegistry(originalList);
        
        TestObserver testObserver = new TestObserver();

        registry.addObserver(testObserver);

        registry.update(null);

        registry.update(originalList);

        assertFalse("Registry should not be changed", testObserver.isChanged());
    }

}
