/*
 * Copyright 2010 the original author or authors.
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
package org.hydracache.data.partition;


/**
 * This interface describe an contract for any object that wishes to be
 * considered consistently hashable. To implement this interface both the native
 * Java hashCode and consistentHashCode methods need to return consistent result
 * with the same given data.
 * 
 * @author Nick Zhu (nzhu@jointsource.com)
 * 
 */
public interface ConsistentHashable {

    /**
     * Native Java hashCode() method redeclared here in this interface to
     * signify that this method has to return consistent value based on the base
     * value, so in short the same base value should always produce the same
     * hashCode
     */
    public int hashCode();

    /**
     * This is the method that {@link HashFunction} will use to compute the hash
     * so just like the hashCode() method this method should also produce
     * consistent value
     */
    public String getConsistentValue();

}
