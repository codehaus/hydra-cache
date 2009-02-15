package org.hydracache.server.httpd.handler;

import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.hydracache.server.data.resolver.ArbitraryResolver;
import org.hydracache.server.data.storage.DataBank;
import org.hydracache.server.data.storage.EhcacheDataBank;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

public class PrintStorageInfoActionTest {
    private Mockery context = new Mockery();

    @Test
    public void ensureCorrectStoragePrint() throws Exception {
        final HttpResponse response = context.mock(HttpResponse.class);

        context.checking(new Expectations() {
            {
                one(response).setEntity(with(any(StringEntity.class)));
            }
        });

        DataBank dataBank = new EhcacheDataBank(new ArbitraryResolver());

        PrintStorageInfoAction handler = new PrintStorageInfoAction(dataBank);
        
        handler.execute(response);

        context.assertIsSatisfied();
    }

}
