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

import org.hydracache.data.partition.ConsistentHashable;

/**
 * Supporting utils for hashing. These methods are based on the Java Memcached Client.
 * 
 * @author Tan Quach
 * @since 1.0
 */
public abstract class HashingUtils {

    /**
     * Get the bytes for a key.
     * 
     * @param k the key
     * @return the bytes
     */
    static byte[] getKeyBytes(final ConsistentHashable k) {
        try {
            String consistentValue = k.getConsistentValue();

            if (consistentValue == null) {
                consistentValue = "null";
            }

            return consistentValue.getBytes("UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the md5 of the given key.
     */
    public static byte[] computeMd5(final ConsistentHashable k) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 not supported", e);
        }
        md5.reset();
        md5.update(getKeyBytes(k));
        return md5.digest();
    }
}
