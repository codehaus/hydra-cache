package org.hydracache.server.httpd.handler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HttpContext;
import org.hydracache.protocol.data.codec.DefaultProtocolEncoder;
import org.hydracache.protocol.data.marshaller.MessageMarshallerFactory;
import org.hydracache.server.harmony.storage.HarmonyDataBank;
import org.jmock.Expectations;
import org.junit.Test;

public class HttpGetMethodHandlerTest extends AbstractHttpMethodHandlerTest {
    private HttpGetMethodHandler handler;

    private HarmonyDataBank dataBank;

    @Override
    public void initialize() {
        super.initialize();

        dataBank = context.mock(HarmonyDataBank.class);
        request = context.mock(HttpEntityEnclosingRequest.class);
        httpContext = context.mock(HttpContext.class);

        createHandler();
    }

    private void createHandler() {
        DefaultProtocolEncoder messageEncoder = new DefaultProtocolEncoder(
                new MessageMarshallerFactory(versionFactoryMarshaller));

        handler = new HttpGetMethodHandler(dataBank, hashFunction,
                messageEncoder);
    }

    @Test
    public void ensureServiceActionExecution() throws Exception {
        Set<HttpServiceAction> actions = new HashSet<HttpServiceAction>();

        HttpServiceAction mockAction = context.mock(HttpServiceAction.class);

        {
            addGetNameExp(mockAction);
            addExecuteExp(mockAction);
        }

        actions.add(mockAction);

        handler.setServiceActions(actions);

        {
            addGetRequestLineExp(request, "/mock/");
        }

        handler.handle(request, response, httpContext);
    }

    @Test
    public void ensureHandleGetDataCorrectly() throws HttpException,
            IOException {
        {
            addGetRequestLineExp(request, "/testKey/");
        }

        {
            addSuccessfulReliableGetExp(dataBank);
        }

        {
            addSetBinaryDataEntityExp(response);
            addSetOkStatusCodeExp(response);
        }

        handler.handle(request, response, httpContext);
    }

    @Test
    public void ensureNotFoundGenerates404() throws Exception {
        {
            addGetRequestLineExp(request, "/testKey/");
        }

        {
            addNotFoundReliableGetExp(dataBank);
        }

        {
            addSetStringMessageEntityExp(response);
            addSetNotFoundStatusCodeExp(response);
        }

        handler.handle(request, response, httpContext);
    }

    protected void addSetNotFoundStatusCodeExp(final HttpResponse response) {
        context.checking(new Expectations() {
            {
                one(response).setStatusCode(HttpStatus.SC_NOT_FOUND);
            }
        });
    }

    @Test
    public void ensureBlankKeyIsRejected() throws HttpException, IOException {
        {
            addGetRequestLineExp(request, "/");
        }

        handler.handle(request, response, httpContext);
    }

}
