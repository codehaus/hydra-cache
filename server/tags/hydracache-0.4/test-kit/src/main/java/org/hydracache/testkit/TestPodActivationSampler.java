package org.hydracache.testkit;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.log4j.Logger;
import org.hydracache.testkit.model.TestPod;
import org.hydracache.testkit.model.TestPodOperator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestPodActivationSampler extends AbstractHydraSampler {
    private static Logger log = Logger
            .getLogger(TestPodActivationSampler.class);

    private static ApplicationContext applicationContext;

    private static final String NUMBER_OF_PODS = "numberOfPods";

    static {
        applicationContext = new ClassPathXmlApplicationContext(
                "/testkit-context.xml");
    }

    private int numberOfPods;

    @Override
    public Arguments getDefaultParameters() {
        Arguments params = super.getDefaultParameters();

        params.addArgument(NUMBER_OF_PODS, "100");

        return params;
    }

    @Override
    public void setupTest(JavaSamplerContext context) {
        super.setupTest(context);

        numberOfPods = context.getIntParameter(NUMBER_OF_PODS);
    }

    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        TestPodOperator podOperator = (TestPodOperator) applicationContext
                .getBean("testPodOperator");

        int podId = RandomUtils.nextInt(numberOfPods);

        SampleResult results = createSampleResult(podId);

        results.sampleStart();

        TestPod pod = podOperator.activateTestPod(podId);

        try {
            client.put(String.valueOf(podId), pod);
        } catch (Exception e) {
            handleException(results, e);
        }

        results.sampleEnd();

        return results;
    }

    private SampleResult createSampleResult(int podId) {
        SampleResult results = new SampleResult();
        results.setSamplerData(String.valueOf(podId));
        results.setSampleLabel("TEST-POD [" + podId + "]");
        return results;
    }

    private void handleException(SampleResult results, Exception e) {
        log.error("Failed test pod sample: ", e);
        results.setSuccessful(false);
        results.setResponseMessage(e.getMessage());
    }
}
