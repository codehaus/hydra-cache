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
package org.hydracache.data.partition;

import org.apache.commons.lang.Validate;


/**
 * @author Nick Zhu (nzhu@jointsource.com)
 * 
 */
public class ConsistentHashableString implements ConsistentHashable {
    private String value;

    public ConsistentHashableString(String value) {
        Validate.notNull(value, "Consistent hashable string can not be null");
        this.value = value;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.data.partition.ConsistentHashable#consistentHash()
     */
    @Override
    public String getConsistentValue() {
        return value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return value.equals(obj);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
