package org.hydracache.client;

import org.hydracache.client.partition.PartitionAwareClient;
import org.hydracache.server.Identity;

import java.util.List;

/**
 * Created by nick.zhu
 */
public class HydraCacheClientFactory {

    public static HydraCacheClient createClient(List<Identity> seedServerIds) {
        return new PartitionAwareClient(seedServerIds);
    }

    public static HydraCacheAdminClient createAdminClient(List<Identity> seedServerIds) {
        return new PartitionAwareClient(seedServerIds);
    }
    
}
