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
package org.hydracache.server;

import java.io.Serializable;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * Common identity class that can be used for identifying server nodes in a
 * group
 * 
 * @author nzhu
 * 
 */
public class Identity implements Serializable, Comparable<Identity> {
    public static final Identity NULL_IDENTITY = getNullIdentity();

    private static final long serialVersionUID = 1L;

    private final InetAddress address;

    private final short port;

    private transient String stringValue;

    private transient int hashCode;

    public Identity(final InetAddress address, final short port) {
        Validate.notNull(address, "Address can not be null");

        this.address = address;
        this.port = port;
    }

    public Identity(final InetAddress address, final int port) {
        this(address, (short) port);
    }

    public Identity(final short port) {
        try {
            this.address = Inet4Address.getLocalHost();
            this.port = port;
        } catch (UnknownHostException e) {
            throw new IllegalStateException("Failed to retrieve local address",
                    e);
        }
    }

    public Identity(final int port) {
        this((short) port);
    }

    public InetAddress getAddress() {
        return address;
    }

    public short getPort() {
        return port;
    }

    @Override
    public String toString() {
        if (stringValue == null)
            stringValue = address.getHostAddress() + ":" + port;

        return stringValue;
    }

    @Override
    public int hashCode() {
        if (hashCode == 0)
            hashCode = toString().hashCode();

        return hashCode;
    }

    @Override
    public boolean equals(final Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int compareTo(Identity o) {
        if (o == null)
            return -2;

        return toString().compareTo(o.toString());
    }

    private static final Identity getNullIdentity() {
        try {
            return new Identity(InetAddress.getByName("127.0.0.1"), 0);
        } catch (UnknownHostException ex) {
            throw new RuntimeException("Failed to create NULL identity", ex);
        }
    }
}
