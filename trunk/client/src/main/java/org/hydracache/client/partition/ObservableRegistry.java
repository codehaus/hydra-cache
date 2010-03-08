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

import java.util.List;
import java.util.Observable;

import org.hydracache.server.Identity;

/**
 * Observable node registry that implements Java util observable pattern
 * 
 * @author Nick Zhu (nzhu@jointsource.com)
 * 
 */
public class ObservableRegistry extends Observable {
    private List<Identity> registry;

    public ObservableRegistry(List<Identity> registry) {
        super();
        this.registry = registry;
    }

    public synchronized void update(List<Identity> newList) {
        clearChanged();

        if (registry == null) {
            setChanged();
        } else if (newList == null) {
            // do nothing
        } else if (sizeDifferenceDetected(newList)) {
            setChanged();
        } else if (!registry.containsAll(newList)) {
            setChanged();
        }

        if (newList != null)
            registry = newList;
        
        notifyObservers(registry);
    }

    private boolean sizeDifferenceDetected(List<Identity> newList) {
        return newList.size() != registry.size();
    }

}
