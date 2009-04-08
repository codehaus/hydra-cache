package org.hydracache.testkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.log4j.Logger;

public class ThroughputPutSampler extends AbstractHydraSampler implements
        JavaSamplerClient {
    private static final int KEY_POOL_SIZE = 50;

    private static Logger log = Logger.getLogger(ThroughputPutSampler.class);

    private List<String> keys = new ArrayList<String>();

    public ThroughputPutSampler() {
        for (int i = 0; i < KEY_POOL_SIZE; i++) {
            keys.add(UUID.randomUUID().toString());
        }
    }

    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        String key = getKey();
        String data = createRandomData();

        SampleResult results = createSampleResult(key, data);

        results.sampleStart();

        try {
            client.put(key, data);
            results.setSuccessful(true);
        } catch (Exception e) {
            log.error("Failed sample: ", e);
            results.setSuccessful(false);
            results.setResponseMessage(e.getMessage());
        }

        results.sampleEnd();

        return results;
    }

    private String getKey() {
        String key = keys.get(RandomUtils.nextInt(KEY_POOL_SIZE));
        return key;
    }

    private SampleResult createSampleResult(String key, String data) {
        SampleResult results = new SampleResult();
        results.setSamplerData(data);
        results.setSampleLabel("PUT [" + key + "]");
        return results;
    }
}
