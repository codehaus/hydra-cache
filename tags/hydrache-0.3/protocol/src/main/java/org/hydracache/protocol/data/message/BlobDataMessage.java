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
package org.hydracache.protocol.data.message;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.data.versioning.Version;

/**
 * Blob data message
 * 
 * @author nzhu
 * 
 */
public class BlobDataMessage implements DataMessage {

    private Data data;

    /**
     * Default constructor
     */
    public BlobDataMessage() {
        this(new Data());
    }

    /**
     * Constructor
     * 
     * @param data
     *            data to be wrapped
     */
    public BlobDataMessage(Data data) {
        super();
        this.data = data;
    }

    public long getKeyHash() {
        return data.getKeyHash();
    }

    public void setKeyHash(long keyHash) {
        data.setKeyHash(keyHash);
    }

    public Version getVersion() {
        return data.getVersion();
    }

    public void setVersion(Version version) {
        data.setVersion(version);
    }

    public byte[] getBlob() {
        return data.getContent();
    }

    public void setBlob(byte[] blob) {
        data.setContent(blob);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.protocol.data.message.DataMessage#getData()
     */
    public Data getData() {
        return data;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hydracache.protocol.data.message.ProtocolMessage#getMessageType()
     */
    @Override
    public short getMessageType() {
        return BLOB_DATA_MESSAGE_TYPE;
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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
