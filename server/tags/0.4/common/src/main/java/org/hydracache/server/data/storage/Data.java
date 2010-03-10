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

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hydracache.server.data.versioning.Version;
import org.hydracache.server.data.versioning.Versioned;

/**
 * Data container
 * 
 * @author nzhu
 * 
 */
public class Data implements Versioned, Serializable {

    private static final long serialVersionUID = 1L;

    private Long keyHash;

    private Version version;

    private byte[] content;

    /**
     * Default constructor 
     */
    public Data() {
        super();
    }

    /**
     * Construct a specific type of data
     * 
     * @param keyHash
     *            data key hash
     * @param version
     *            current version
     * @param content
     *            data content
     */
    public Data(Long keyHash, Version version, byte[] content) {
        this.keyHash = keyHash;
        this.version = version;
        this.content = content;
    }

    /**
     * Constructor
     * 
     * @param keyHash
     */
    public Data(Long keyHash) {
        this(keyHash, null, null);
    }

    public Long getKeyHash() {
        return keyHash;
    }

    public void setKeyHash(Long key) {
        this.keyHash = key;
    }

    @Override
    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

}
