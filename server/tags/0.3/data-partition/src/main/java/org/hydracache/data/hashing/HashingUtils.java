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
package org.hydracache.data.hashing;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Supporting utils for hashing. These methods are based on the Java Memcached
 * Client.
 * 
 * @author Tan Quach
 * @since 1.0
 */
public abstract class HashingUtils {
    /**
     * Get the bytes for a key.
     * 
     * @param k
     *            the key
     * @return the bytes
     */
    public static byte[] getKeyBytes(String k) {
        try {
            return k.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Get the bytes for a key.
     * 
     * @param k
     *            the key
     * @return the bytes
     */
    public static byte[] getKeyBytes(Object k) {
        try {
            return String.valueOf(k).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Get the keys in byte form for all of the string keys.
     * 
     * @param keys
     *            a collection of keys
     * @return return a collection of the byte representations of keys
     */
    public static Collection<byte[]> getKeyBytes(Collection<String> keys) {
        Collection<byte[]> rv = new ArrayList<byte[]>(keys.size());
        for (String s : keys) {
            rv.add(getKeyBytes(s));
        }
        return rv;
    }

    /**
     * Get the md5 of the given key.
     */
    public static byte[] computeMd5(Object k) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 not supported", e);
        }
        md5.reset();
        md5.update(getKeyBytes(k));
        return md5.digest();
    }
}
