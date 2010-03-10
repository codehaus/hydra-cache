package org.hydracache.testkit;

import org.apache.jmeter.protocol.java.sampler.JavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.log4j.Logger;

public class ThroughputGetSampler extends AbstractHydraSampler implements
        JavaSamplerClient {
    private static Logger log = Logger.getLogger(ThroughputGetSampler.class);
    
    private String key;

    public ThroughputGetSampler() {
    }

    @Override
    public void setupTest(JavaSamplerContext context) {
        super.setupTest(context);

        try {
            key = getRandomKey();
            String data = createRandomData();
            client.put(key, data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        SampleResult results = createSampleResult(key);

        results.sampleStart();

        try {
            Object data = client.get(key);
            results.setSuccessful(true);
            results.setResponseMessage(data.toString());
        } catch (Exception e) {
            log.error("Failed sample: ", e);
            results.setSuccessful(false);
            results.setResponseMessage(e.getMessage());
        }

        results.sampleEnd();

        return results;
    }

    private SampleResult createSampleResult(String key) {
        SampleResult results = new SampleResult();
        results.setSamplerData(key);
        results.setSampleLabel("GET [" + key + "]");
        return results;
    }
}
