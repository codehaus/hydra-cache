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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hydracache.server.Identity;

/**
 * @author David Dossot (david@dossot.net)
 */
final class VectorClock implements Version {

    private final static class EntrySearchResult {
        VectorClockEntry entry;
        int index;

        EntrySearchResult(final int index, final VectorClockEntry entry) {
            this.index = index;
            this.entry = entry;
        }
    }

    private final List<VectorClockEntry> entries;

    VectorClock(final Identity nodeId) {
        entries = Collections.singletonList(new VectorClockEntry(nodeId, 1L));
    }

    VectorClock(final List<VectorClockEntry> newEntries) {
        Validate.notNull(newEntries, "newEntries can not be null");

        entries = Collections.unmodifiableList(new ArrayList<VectorClockEntry>(
                newEntries));
    }

    /**
     * @param index
     *            a negative value means: append to list.
     */
    private VectorClock(final VectorClock previousVectorClock, final int index,
            final VectorClockEntry entry) {

        Validate.notNull(previousVectorClock,
                "previousVectorClock can not be null");

        final List<VectorClockEntry> newEntries = new ArrayList<VectorClockEntry>(
                previousVectorClock.entries);

        if (entry != null) {
            if ((index >= 0) && (index < newEntries.size())) {
                newEntries.set(index, entry);
            } else {
                newEntries.add(entry);
            }
        }

        entries = Collections.unmodifiableList(newEntries);
    }

    private EntrySearchResult findExistingEntryFor(final Identity nodeId) {
        for (int i = 0; i < entries.size(); i++) {
            final VectorClockEntry entry = entries.get(i);

            if (entry.getNodeId().equals(nodeId)) {
                return new EntrySearchResult(i, entry);
            }
        }

        return null;
    }

    List<VectorClockEntry> getEntries() {
        return entries;
    }

    public Version incrementFor(final Identity nodeId) {
        final EntrySearchResult entrySearchResult = findExistingEntryFor(nodeId);

        if (entrySearchResult == null) {
            return new VectorClock(this, -1, new VectorClockEntry(nodeId, 1L));
        }

        return new VectorClock(this, entrySearchResult.index,
                entrySearchResult.entry.increment());
    }

    /**
     * A vector clock descends from another one if it is not the same and all
     * the entries of the other one are the same or descend from its entries.
     */
    public boolean isDescendantOf(final Version other) {
        if ((!(other instanceof VectorClock)) || (this.equals(other))) {
            return false;
        }

        final VectorClock otherVectorClock = (VectorClock) other;

        if (entries.size() < otherVectorClock.entries.size()) {
            return false;
        }

        for (final VectorClockEntry otherEntry : otherVectorClock.entries) {
            final EntrySearchResult existingEntryForOtherNodeId = findExistingEntryFor(otherEntry
                    .getNodeId());

            if (existingEntryForOtherNodeId == null) {
                return false;
            }

            if (notEqualsAndNotDescendant(existingEntryForOtherNodeId,
                    otherEntry)) {
                return false;
            }
        }

        return true;
    }

    private boolean notEqualsAndNotDescendant(
            final EntrySearchResult existingEntryForOtherNodeId,
            final VectorClockEntry otherEntry) {

        return (!existingEntryForOtherNodeId.entry.equals(otherEntry))
                && (!existingEntryForOtherNodeId.entry.descendFrom(otherEntry));
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
