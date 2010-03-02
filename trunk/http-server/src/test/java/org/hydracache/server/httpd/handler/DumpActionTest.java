package org.hydracache.server.httpd.handler;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.http.entity.StringEntity;
import org.hydracache.server.data.resolver.ArbitraryResolver;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.data.storage.DataBank;
import org.hydracache.server.data.storage.EhcacheDataBank;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class DumpActionTest extends AbstractJsonServiceActionTest {
    @Test
    public void ensureCorrectStoragePrint() throws Exception {
        DataBank dataBank = new EhcacheDataBank(new ArbitraryResolver());

        dataBank.put("testContext", new Data(RandomUtils.nextLong()));
        dataBank.put("testContext", new Data(RandomUtils.nextLong()));

        stubRequestWithEmptyParams();

        ArgumentCaptor<StringEntity> captor = ArgumentCaptor
                .forClass(StringEntity.class);

        DumpAction handler = new DumpAction(dataBank);

        handler.execute(mockRequest, mockResponse);

        verify(mockResponse, times(1)).setEntity(captor.capture());

        String printOutput = IOUtils.toString(captor.getValue().getContent());

        assertTrue("JSON output is incorrect", printOutput
                .contains("[{\"keyHash\":"));
        assertTrue("JSON output is incorrect", printOutput
                .contains(",\"size\":"));
    }

}
