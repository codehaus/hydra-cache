package org.hydracache.client;

import org.hydracache.client.partition.PartitionAwareClient;
import org.hydracache.client.partition.PartitionUpdatesPoller;
import org.hydracache.server.Identity;

import java.util.List;

/**
 * Created by nick.zhu
 */
public class HydraCacheClientFactory {
    // TODO: interval should be configurable
    private static final int DEFAULT_PARTITION_UPDATE_INTERVAL = 180000;

    public HydraCacheClient createClient(List<Identity> seedServerIds) {
        PartitionUpdatesPoller poller = new PartitionUpdatesPoller(
                seedServerIds, DEFAULT_PARTITION_UPDATE_INTERVAL);
        PartitionAwareClient client = new PartitionAwareClient(seedServerIds, poller);

        poller.addListener(client);
        poller.setAdminClient(client);

        return client;
    }

    public HydraCacheAdminClient createAdminClient(List<Identity> seedServerIds) {
        // PartitionAwareClient implements both interfaces
        return (HydraCacheAdminClient) createClient(seedServerIds);
    }

}
