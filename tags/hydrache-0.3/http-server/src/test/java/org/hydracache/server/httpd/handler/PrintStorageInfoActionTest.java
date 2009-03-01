package org.hydracache.server.httpd.handler;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.hydracache.server.data.resolver.ArbitraryResolver;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.data.storage.DataBank;
import org.hydracache.server.data.storage.EhcacheDataBank;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

public class PrintStorageInfoActionTest {
    private Mockery context = new Mockery();

    @Test
    public void ensureCorrectStoragePrint() throws Exception {
        final HttpResponse response = mockHttpResponse();

        DataBank dataBank = new EhcacheDataBank(new ArbitraryResolver());
        
        dataBank.put(new Data(RandomUtils.nextLong()));
        dataBank.put(new Data(RandomUtils.nextLong()));

        PrintStorageInfoAction handler = new PrintStorageInfoAction(dataBank);

        handler.execute(response);

        context.assertIsSatisfied();
    }

    private HttpResponse mockHttpResponse() {
        final HttpResponse response = context.mock(HttpResponse.class);

        context.checking(new Expectations() {
            {
                one(response).setEntity(with(any(StringEntity.class)));
            }
        });
        return response;
    }

}
