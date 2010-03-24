package org.hydracache.client;

import org.hydracache.client.partition.PartitionAwareClient;
import org.hydracache.server.Identity;

import java.util.List;

/**
 * Created by nick.zhu
 */
public class HydraCacheClientFactory {

    public HydraCacheClient createClient(List<Identity> seedServerIds) {
        return new PartitionAwareClient(seedServerIds);
    }

    public HydraCacheAdminClient createAdminClient(List<Identity> seedServerIds) {
        return new PartitionAwareClient(seedServerIds);
    }
    
}
