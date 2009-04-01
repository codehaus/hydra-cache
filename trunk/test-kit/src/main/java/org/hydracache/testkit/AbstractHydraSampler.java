package org.hydracache.testkit;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.hydracache.client.http.PartitionAwareHydraCacheClient;
import org.hydracache.data.hashing.KetamaBasedHashFunction;
import org.hydracache.data.partitioning.ConsistentHashNodePartition;
import org.hydracache.data.partitioning.NodePartition;
import org.hydracache.server.Identity;
import org.hydracache.server.IdentityMarshaller;
import org.hydracache.server.data.versioning.IncrementVersionFactory;
import org.json.JSONArray;
import org.json.JSONObject;

public abstract class AbstractHydraSampler extends AbstractJavaSamplerClient {

    private static final String PORT = "port";

    private static final String IP = "ip";

    protected static final int SAMPLE_SIZE = 1000;
    
    private static final String SEED_SERVER_LIST = "seedServerList";

    protected static final String LOCALHOST = "localhost";

    protected PartitionAwareHydraCacheClient client;

    private String seedServerListParam;

    private List<Identity> seedServerIds;

    public AbstractHydraSampler() {
        super();
    }

    @Override
    public Arguments getDefaultParameters() {
        Arguments params = new Arguments();

        params.addArgument(SEED_SERVER_LIST, "[{\"" + PORT + "\":8080,\"" + IP
                + "\":\"127.0.0.1\"}]");

        return params;
    }

    @Override
    public void setupTest(JavaSamplerContext context) {
        try {
            contructSeedServerIds(context);
            client = createHydraClient();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void contructSeedServerIds(JavaSamplerContext context)
            throws Exception {
        seedServerListParam = context.getParameter(SEED_SERVER_LIST);

        seedServerIds = new ArrayList<Identity>();

        JSONArray seedServerArray = new JSONArray(seedServerListParam);
        for (int i = 0; i < seedServerArray.length(); i++) {
            JSONObject seedServer = seedServerArray.getJSONObject(i);

            seedServerIds.add(new Identity(InetAddress.getByName(seedServer
                    .getString(IP)), seedServer.getInt(PORT)));
        }
    }

    protected PartitionAwareHydraCacheClient createHydraClient() {
        IncrementVersionFactory versionFactory = new IncrementVersionFactory();

        versionFactory.setIdentityMarshaller(new IdentityMarshaller());

        NodePartition<Identity> partition = new ConsistentHashNodePartition<Identity>(
                new KetamaBasedHashFunction(), seedServerIds);

        PartitionAwareHydraCacheClient client = new PartitionAwareHydraCacheClient(
                partition);

        return client;
    }

    @Override
    public void teardownTest(JavaSamplerContext context) {
    }

    protected String getRandomKey() {
        String key = UUID.randomUUID().toString();
        return key;
    }

}