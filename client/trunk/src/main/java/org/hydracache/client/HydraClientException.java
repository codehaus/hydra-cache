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
package org.hydracache.client;

/**
 * Specific runtime exception class for client side errors.
 * 
 * @author Tan Quach
 * @since 1.0
 */
public class HydraClientException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public HydraClientException() {
        super();
    }

    public HydraClientException(String message, Throwable parentError) {
        super(message, parentError);
    }

    public HydraClientException(String message) {
        super(message);
    }

    public HydraClientException(Throwable parentError) {
        super(parentError);
    }
}
