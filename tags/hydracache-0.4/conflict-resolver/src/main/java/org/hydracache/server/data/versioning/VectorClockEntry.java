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

import java.util.Collections;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hydracache.server.Identity;

/**
 * @author David Dossot (david@dossot.net)
 */
final class VectorClockEntry {
    private final Identity nodeId;
    private final long value;
    private final long timestamp;

    public VectorClockEntry(final Identity nodeId, final long value) {
        this(nodeId, value, System.currentTimeMillis());
    }

    public VectorClockEntry(final Identity nodeId, final long value,
            final long timeStamp) {

        Validate.notNull(nodeId, "nodeId can not be null");
        this.nodeId = nodeId;
        this.value = value;
        this.timestamp = timeStamp;
    }

    public VectorClockEntry increment() {
        return new VectorClockEntry(nodeId, value + 1L);
    }

    public boolean descendFrom(final VectorClockEntry other) {
        if (this.equals(other)) {
            return false;
        }

        if (!nodeId.equals(other.nodeId)) {
            return false;
        }

        return value > other.value;
    }

    public Identity getNodeId() {
        return nodeId;
    }

    public long getValue() {
        return value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(final Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj, Collections
                .singleton("timestamp"));
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, Collections
                .singleton("timestamp"));
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.SIMPLE_STYLE);
    }

}