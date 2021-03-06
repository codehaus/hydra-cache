package org.hydracache.client;

import org.hydracache.server.Identity;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.Assert.*;

/**
 * Created by nick.zhu
 */
public class HydraCacheClientFactoryTest {

    @Test
    public void ensureCanCreateClient() {
        HydraCacheClient client = new HydraCacheClientFactory().createClient(
                Arrays.asList(new Identity(80)));

        assertNotNull("Client can't be null", client);
    }

    @Test
    public void ensureCanCreateAdminClient(){
        HydraCacheAdminClient client = new HydraCacheClientFactory().createAdminClient(
                Arrays.asList(new Identity(80)));

        assertNotNull("Client can't be null", client);
    }

}
