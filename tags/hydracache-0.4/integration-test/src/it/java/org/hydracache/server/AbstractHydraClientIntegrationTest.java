package org.hydracache.server;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;

public class AbstractHydraClientIntegrationTest {

    protected static final int KEY_POOL_SIZE = 50;

    protected static final List<String> keyPool = new ArrayList<String>();

    static {
        for (int i = 0; i < KEY_POOL_SIZE; i++) {
            keyPool.add(UUID.randomUUID().toString());
        }
    }

    public AbstractHydraClientIntegrationTest() {
        super();
    }

    protected String getKeyFromThePool() {
        String randomKey = keyPool.get(RandomUtils.nextInt(KEY_POOL_SIZE));
        return randomKey;
    }

    protected String createRandomDataSample(String randomKey) {
        return RandomStringUtils.randomAlphanumeric(200);
    }

}