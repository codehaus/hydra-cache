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
package org.hydracache.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Helper class for IO operations
 * 
 * @author nzhu
 * 
 */
public final class IoUtils {

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    private IoUtils() {
    }

    public static byte[] readRemainingBytes(InputStream in)
            throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

        int len = in.read(buffer);

        while (len >= 0) {
            out.write(buffer, 0, len);
            len = in.read(buffer);
        }

        out.close();

        byte[] bytes = out.toByteArray();

        return bytes;
    }

}
