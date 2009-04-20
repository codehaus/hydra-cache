package org.hydracache.server.httpd.handler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.RequestLine;
import org.apache.http.protocol.HttpContext;
import org.hydracache.protocol.data.codec.DefaultProtocolEncoder;
import org.hydracache.protocol.data.marshaller.MessageMarshallerFactory;
import org.hydracache.server.harmony.storage.HarmonyDataBank;
import org.jmock.Expectations;
import org.junit.Test;

public class HttpGetMethodHandlerTest extends AbstractHttpMethodHandlerTest {
    private HttpGetMethodHandler handler;

    private HarmonyDataBank dataBank;

    private HttpRequest request;

    private HttpContext httpContext;

    @Override
    public void initialize() {
        super.initialize();

        dataBank = context.mock(HarmonyDataBank.class);
        request = context.mock(HttpRequest.class);
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
            addGetRequestLineExp(request);
        }

        handler.handle(request, response, httpContext);
    }

    private void addExecuteExp(final HttpServiceAction mockAction)
            throws HttpException, IOException {
        context.checking(new Expectations() {
            {
                one(mockAction).execute(with(any(HttpResponse.class)));
            }
        });
    }

    private void addGetNameExp(final HttpServiceAction mockAction) {
        context.checking(new Expectations() {
            {
                one(mockAction).getName();
                will(returnValue("mock"));
            }
        });
    }

    private void addGetRequestLineExp(final HttpRequest request) {
        context.checking(new Expectations() {
            {
                one(request).getRequestLine();
                final RequestLine requestLine = context.mock(RequestLine.class);
                context.checking(new Expectations() {
                    {
                        one(requestLine).getUri();
                        will(returnValue("/mock/"));
                    }
                });
                will(returnValue(requestLine));
            }
        });
    }

}
