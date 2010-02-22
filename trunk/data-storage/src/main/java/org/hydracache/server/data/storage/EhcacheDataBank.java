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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.hydracache.server.data.resolver.ConflictResolver;
import org.hydracache.server.data.resolver.ResolutionResult;
import org.hydracache.server.data.versioning.VersionConflictException;
import org.hydracache.server.data.versioning.Versioned;

/**
 * A {@link DataBank} implementation using Ehcache library
 * 
 * @author nzhu
 * 
 */
public class EhcacheDataBank implements DataBank {
    private static Logger log = Logger.getLogger(EhcacheDataBank.class);

    /**
     * Ehcache configuration file name
     */
    public static final String DEFAULT_CACHE_CONF_FILE_NAME = "cache.xml";

    private final CacheManager cacheManager;

    private final ConflictResolver conflictResolver;

    /**
     * By default this constructor implementation loads Ehcache manager
     * configuration from file {@link DEFAULT_CACHE_CONF_FILE_NAME} in classpath
     * 
     * @param conflictResolver
     *            conflict resolver that will be used to resolve data versioning
     *            conflict
     */
    public EhcacheDataBank(final ConflictResolver conflictResolver) {
        this(conflictResolver, CacheManager.create(EhcacheDataBank.class
                .getClassLoader().getResourceAsStream(
                        DEFAULT_CACHE_CONF_FILE_NAME)));
    }

    /**
     * Create a new {@link EhcacheDataBank} instance using the given
     * {@link CacheManager} and if this given {@link CacheManager} does not
     * contain a specific cache intance named {@link CACHE_NAME} this contructor
     * will create one using the default configuration
     * 
     * @param conflictResolver
     *            conflict resolver that will be used to resolve data versioning
     *            conflict
     * @param cacheManager
     *            cache manager to use
     */
    public EhcacheDataBank(final ConflictResolver conflictResolver,
            final CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        this.conflictResolver = conflictResolver;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.server.data.storage.DataBank#get(java.lang.Long)
     */
    @Deprecated
    @Override
    public Data get(final Long keyHash) throws IOException {
        return get(DEFAULT_CACHE_CONTEXT_NAME, keyHash);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.server.data.storage.DataBank#get(java.lang.String,
     * java.lang.Long)
     */
    @Override
    public Data get(String context, Long keyHash) throws IOException {
        if(StringUtils.isBlank(context)){
            context = DEFAULT_CACHE_CONTEXT_NAME;
        }
        
        Validate.isTrue(keyHash != null, "Data key hash can not be null");

        Cache cache = acquireCache(context);

        final Element element = cache.get(keyHash);

        if (element == null)
            return null;

        final Serializable data = element.getValue();

        Validate.isTrue(data instanceof Data,
                "Cache element contains unknown data[" + data + "]");

        return (Data) data;
    }

    private Cache acquireCache(String cacheName) {
        if (!cacheManager.cacheExists(cacheName))
            cacheManager.addCache(cacheName);

        Cache cache = cacheManager.getCache(cacheName);

        return cache;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.server.data.storage.DataBank#getAll()
     */
    @SuppressWarnings("unchecked")
    @Override
    public Collection<Data> getAll() throws IOException {
        List<Data> results = new ArrayList<Data>();
        String[] cacheNames = cacheManager.getCacheNames();

        for (int i = 0; i < cacheNames.length; i++) {
            String cacheName = cacheNames[i];
            Cache cache = cacheManager.getCache(cacheName);
            List keys = cache.getKeys();

            for (Object eachKey : keys) {
                if (eachKey instanceof Long)
                    results.add(get(cacheName, (Long) eachKey));
                else
                    log.warn("Skipping unknown key: " + eachKey);
            }
        }

        return results;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hydracache.server.data.storage.DataBank#put(org.hydracache.server
     * .data.storage.Data)
     */
    @Deprecated
    @Override
    public void put(final Data newData) throws IOException,
            VersionConflictException {
        put(DEFAULT_CACHE_CONTEXT_NAME, newData);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.server.data.storage.DataBank#put(java.lang.String,
     * org.hydracache.server.data.storage.Data)
     */
    @Override
    public void put(String context, Data data) throws IOException,
            VersionConflictException {
        if(StringUtils.isBlank(context)){
            context = DEFAULT_CACHE_CONTEXT_NAME;
        }
        
        Cache cache = acquireCache(context);

        Validate.notNull(data, "Data object can not be null");

        Data dataToStore = data;

        if (dataAlreadyExists(context, data)) {
            final Data oldData = get(context, data.getKeyHash());

            final List<Versioned> conflictList = buildConflictList(data,
                    oldData);

            final ResolutionResult resolutionResult = conflictResolver
                    .resolve(conflictList);

            if (resolutionResult.stillHasConflict())
                throw new DataConflictException(
                        "Unresolvable version conflict detected for data["
                                + data + "]");

            dataToStore = getMostAliveVersion(resolutionResult);
        }

        final Element element = new Element(data.getKeyHash(), dataToStore);

        cache.put(element);
    }

    private Data getMostAliveVersion(final ResolutionResult resolutionResult) {
        return (Data) resolutionResult.getAlive().iterator().next();
    }

    private List<Versioned> buildConflictList(final Versioned newData,
            final Versioned oldData) {

        return Arrays.asList(oldData, newData);
    }

    private boolean dataAlreadyExists(final String context, final Data newData)
            throws IOException {
        return get(context, newData.getKeyHash()) != null;
    }
}
