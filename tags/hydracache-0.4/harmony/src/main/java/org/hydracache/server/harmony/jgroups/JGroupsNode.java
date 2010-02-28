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
package org.hydracache.server.harmony.jgroups;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hydracache.server.Identity;
import org.hydracache.server.harmony.core.Node;
import org.jgroups.Address;

/**
 * Basic JGroups based HOP node implementation
 * 
 * @author nzhu
 * 
 */
public class JGroupsNode implements Node {

    private static final long serialVersionUID = 1L;

    private Identity id;

    private Address jgroupsAddress;

    public JGroupsNode(Identity id, Address jgroupsAddress) {
        super();
        this.id = id;
        this.jgroupsAddress = jgroupsAddress;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.server.harmony.Node#getId()
     */
    @Override
    public Identity getId() {
        return id;
    }

    public Address getJgroupsAddress() {
        return jgroupsAddress;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, new String[]{"jgroupsAddress"});
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj, new String[]{"jgroupsAddress"});
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Node o) {
        if (o == null)
            return -2;

        return id.compareTo(o.getId());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return id.toString();
    }

}
