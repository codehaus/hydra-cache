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
package org.hydracache.client.partition;

import java.util.List;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.hydracache.client.HydraCacheAdminClient;
import org.hydracache.server.Identity;

/**
 * Periodically polls the server for updates to the server partition nodes.
 * 
 * @author Tan Quach (tquach@jointsource.com)
 * @since 1.0
 */
public class PartitionUpdatesPoller extends Thread {
    private static final Logger logger = Logger
            .getLogger(PartitionUpdatesPoller.class);

    private HydraCacheAdminClient adminClient;
    private ObservableRegistry registry;
    private final long interval;
    private boolean running;

    /**
     * Provide a reference to an admin client and at least one observer.
     */
    public PartitionUpdatesPoller(List<Identity> seedServerIds, long interval) {
        this.interval = interval;

        this.registry = new ObservableRegistry(seedServerIds);
    }

    public void setAdminClient(HydraCacheAdminClient adminClient) {
        this.adminClient = adminClient;
    }

    public void addListener(Observer listener){
        registry.addObserver(listener);
    }

    /*
    * (non-Javadoc)
    *
    * @see java.lang.Thread#run()
    */
    @Override
    public void run() {
        running = true;

        List<Identity> list;

        while (running) {
            try {
                logger.info("Updating node list.");
                list = adminClient.listNodes();

                logger.info("Registry: " + list);
                registry.update(list);
            } catch (Exception e) {
                logger.error("Failed to updated node registry", e);
            } finally {
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException iex) {
                    // do nothing
                }
            }
        }

        logger.info("Shutting down poller thread.");
    }

    public void shutdown() {
        running = false;
    }
}
