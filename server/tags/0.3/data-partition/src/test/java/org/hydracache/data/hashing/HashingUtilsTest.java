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

import org.junit.Assert;
import org.junit.Test;


/**
 * @author Tan Quach
 * @since 1.0
 */
public class HashingUtilsTest {

    @Test
    public void shouldComputeMd5() throws Exception {
        Assert.assertNotNull(HashingUtils.computeMd5(new Integer(10)));
    }
    
    @Test
    public void shouldGetKeyBytes() throws Exception {
       byte[] keyBytes = HashingUtils.getKeyBytes("bytes");
       Assert.assertNotNull(keyBytes);
    }
}
