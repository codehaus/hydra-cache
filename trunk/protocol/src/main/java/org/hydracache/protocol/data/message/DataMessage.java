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

import java.io.Serializable;

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
public class DataMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final short BLOB_DATA_MESSAGE_TYPE = 100;

    private Version version;

    private byte[] blob;

    /**
     * Default constructor
     */
    public DataMessage() {
    }

    /**
     * Constructor
     * 
     * @param data
     *            data to be wrapped
     */
    public DataMessage(Data data) {
        this.version = data.getVersion();
        this.blob = data.getContent();
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public byte[] getBlob() {
        return blob.clone();
    }

    public void setBlob(byte[] blob) {
        if (blob == null)
            this.blob = new byte[0];
        else
            this.blob = blob.clone();
    }

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
