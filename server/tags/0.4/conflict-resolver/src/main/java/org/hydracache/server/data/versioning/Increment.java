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
package org.hydracache.server.data.versioning;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hydracache.server.Identity;

/**
 * A basic increment-based versioning mechanism that is ignores the node when
 * resolving conflicts: the highest number wins.
 * 
 * @author David Dossot (david@dossot.net)
 */
final class Increment implements Version, Serializable {

    private static final long serialVersionUID = 1L;

    private final long value;

    private final Identity nodeId;

    Increment(final Identity nodeId) {
        this(nodeId, 1L);
    }

    Increment(final Identity nodeId, final long value) {
        this.nodeId = nodeId;
        this.value = value;
    }

    long getValue() {
        return value;
    }

    Identity getNodeId() {
        return nodeId;
    }

    @Override
    public Version incrementFor(final Identity nodeId) {
        return new Increment(nodeId, value + 1L);
    }

    @Override
    public boolean isDescendantOf(final Version other) {
        if ((!(other instanceof Increment)) || (this.equals(other))) {
            return false;
        }

        final Increment otherIncrement = (Increment) other;

        return value > otherIncrement.value;
    }

    @Override
    public boolean equals(final Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.SIMPLE_STYLE);
    }

}
