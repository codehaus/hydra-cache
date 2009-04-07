package org.hydracache.server;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;

public class AbstractHydraClientIntegrationTest {

    protected static final int SAMPLE_SIZE = 10;

    protected static final List<String> keys = new ArrayList<String>();

    static {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            keys.add(UUID.randomUUID().toString());
        }
    }

    public AbstractHydraClientIntegrationTest() {
        super();
    }

    protected String createRandomKey() {
        String randomKey = keys.get(RandomUtils.nextInt(SAMPLE_SIZE));
        return randomKey;
    }

    protected String createRandomDataSample(String randomKey) {
        return RandomStringUtils.randomAlphanumeric(200);
    }

}