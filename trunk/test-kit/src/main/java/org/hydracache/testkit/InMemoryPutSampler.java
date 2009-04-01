package org.hydracache.testkit;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.log4j.Logger;

public class InMemoryPutSampler extends AbstractHydraSampler implements JavaSamplerClient {
    private static Logger log = Logger.getLogger(InMemoryPutSampler.class);

    public InMemoryPutSampler() {
    }

    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        String key = getRandomKey();
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

    private String createRandomData() {
        return RandomStringUtils.randomAlphanumeric(200);
    }

    private SampleResult createSampleResult(String key, String data) {
        SampleResult results = new SampleResult();
        results.setSamplerData(data);
        results.setSampleLabel("PUT [" + key + "]");
        return results;
    }
}
