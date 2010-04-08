package org.hydracache.server.httpd.handler;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.http.entity.StringEntity;
import org.hydracache.server.data.resolver.ArbitraryResolver;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.data.storage.DataBank;
import org.hydracache.server.data.storage.EhcacheDataBank;
import org.hydracache.server.data.versioning.VersionConflictException;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class PrintStorageInfoActionTest extends AbstractJsonServiceActionTest {
    @Test
    public void ensureCorrectStoragePrint() throws Exception {
        stubRequestWithEmptyParams();

        DataBank dataBank = buildMockDataBank();

        ArgumentCaptor<StringEntity> captor = ArgumentCaptor
                .forClass(StringEntity.class);

        PrintStorageInfoAction handler = new PrintStorageInfoAction(dataBank);

        handler.execute(mockRequest, mockResponse);

        verify(mockResponse, times(1)).setEntity(captor.capture());

        String printOutput = IOUtils.toString(captor.getValue().getContent());

        assertTrue("JSON output is incorrect", printOutput
                .contains("\"totalMemory\":"));
        assertTrue("JSON output is incorrect", printOutput
                .contains(",\"size\":2}"));

    }

    private DataBank buildMockDataBank() throws IOException,
            VersionConflictException {
        DataBank dataBank = new EhcacheDataBank(new ArbitraryResolver());

        dataBank.put("testContext", new Data(RandomUtils.nextLong()));
        dataBank.put("testContext", new Data(RandomUtils.nextLong()));
        return dataBank;
    }
    
    @Test
    public void ensureCorrectStoragePrintWithPadding() throws Exception {
        stubJSonPHandlerParam("testHandler");

        DataBank dataBank = buildMockDataBank();

        ArgumentCaptor<StringEntity> captor = ArgumentCaptor
                .forClass(StringEntity.class);

        PrintStorageInfoAction handler = new PrintStorageInfoAction(dataBank);

        handler.execute(mockRequest, mockResponse);

        verify(mockResponse, times(1)).setEntity(captor.capture());

        String printOutput = IOUtils.toString(captor.getValue().getContent());

        assertTrue("JSON output is missing the padding", printOutput
                .startsWith("testHandler("));
        assertTrue("JSON output is incorrect", printOutput
                .contains("\"totalMemory\":"));
        assertTrue("JSON output is incorrect", printOutput
                .contains(",\"size\":"));

    }

}
