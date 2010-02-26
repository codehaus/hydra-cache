package org.hydracache.server.httpd.handler;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.hydracache.data.hashing.HashFunction;
import org.hydracache.data.hashing.KetamaBasedHashFunction;
import org.hydracache.protocol.data.codec.DefaultProtocolEncoder;
import org.hydracache.protocol.data.marshaller.DataMessageMarshaller;
import org.hydracache.protocol.data.marshaller.DataMessageXmlMarshaller;
import org.hydracache.server.Identity;
import org.hydracache.server.IdentityMarshaller;
import org.hydracache.server.IdentityXmlMarshaller;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.data.versioning.IncrementVersionFactory;
import org.hydracache.server.data.versioning.VersionXmlMarshaller;
import org.hydracache.server.harmony.storage.HarmonyDataBank;
import org.junit.Test;

public class HttpGetMethodHandlerTest extends AbstractHttpMethodHandlerTest {
    public HttpGetMethodHandlerTest() {
    }

    @Test
    public void ensureHandleGetXmlDataCorrectly() throws HttpException,
            IOException {

        BaseHttpMethodHandler handler = createHttpGetMethodHandler();

        stubGetRequestURI(mockRequest, "/testContext/testKey?protocol=xml");
        stubGetProtocolParam(BaseHttpMethodHandler.XML_PROTOCOL);
        stubSuccessfulReliableGet(mockDataBank);

        handler.handle(mockRequest, mockResponse, mockHttpContext);

        verify(mockResponse).setStatusCode(HttpStatus.SC_OK);
        verify(mockResponse).setEntity(any(StringEntity.class));
    }

    private HttpGetMethodHandler createHttpGetMethodHandler() {
        HashFunction hashFunction = new KetamaBasedHashFunction();

        IncrementVersionFactory versionMarshaller = new IncrementVersionFactory(
                new IdentityMarshaller());

        DefaultProtocolEncoder messageEncoder = new DefaultProtocolEncoder(
                new DataMessageMarshaller(versionMarshaller),
                new DataMessageXmlMarshaller(new VersionXmlMarshaller(
                        new IdentityXmlMarshaller(), versionMarshaller)));

        HttpGetMethodHandler handler = new HttpGetMethodHandler(mockDataBank,
                hashFunction, messageEncoder);

        return handler;
    }

    private void stubSuccessfulReliableGet(HarmonyDataBank mockDataBank)
            throws IOException {
        Data data = new Data();
        data.setVersion(new IncrementVersionFactory().create(new Identity(70)));
        when(mockDataBank.get(anyString(), anyLong())).thenReturn(data);
    }

    @Test
    public void ensureHandlerDefaultsToBinary() throws HttpException,
            IOException {

        BaseHttpMethodHandler handler = createHttpGetMethodHandler();

        stubGetRequestURI(mockRequest, "/testContext/testKey?protocol=unknown");
        stubGetProtocolParam("unknown");
        stubSuccessfulReliableGet(mockDataBank);

        handler.handle(mockRequest, mockResponse, mockHttpContext);

        verify(mockResponse).setStatusCode(HttpStatus.SC_OK);
        verify(mockResponse).setEntity(any(ByteArrayEntity.class));
    }

    @Test
    public void ensureServiceActionExecution() throws Exception {
        Set<HttpServiceAction> actions = new HashSet<HttpServiceAction>();

        HttpServiceAction mockAction = mock(HttpServiceAction.class);

        when(mockAction.getName()).thenReturn("mock");

        actions.add(mockAction);

        HttpGetMethodHandler handler = createHttpGetMethodHandler();

        handler.setServiceActions(actions);

        stubGetRequestURI(mockRequest, "/mock");

        handler.handle(mockRequest, mockResponse, mockHttpContext);

        verify(mockAction).execute(mockResponse);
    }

    @Test
    public void ensureHandleGetDataCorrectly() throws HttpException,
            IOException {
        stubGetRequestURI(mockRequest, "/testContext/testKey");
        stubSuccessfulReliableGet(mockDataBank);

        BaseHttpMethodHandler handler = createHttpGetMethodHandler();

        handler.handle(mockRequest, mockResponse, mockHttpContext);

        verify(mockResponse).setStatusCode(HttpStatus.SC_OK);
        verify(mockResponse).setEntity(any(ByteArrayEntity.class));
    }

    @Test
    public void ensureNotFoundGenerates404() throws Exception {
        stubGetRequestURI(mockRequest, "/testContext/testKey");

        BaseHttpMethodHandler handler = createHttpGetMethodHandler();

        handler.handle(mockRequest, mockResponse, mockHttpContext);

        verify(mockResponse).setStatusCode(HttpStatus.SC_NOT_FOUND);
        verify(mockResponse).setEntity(any(StringEntity.class));
    }

    @Test
    public void ensureBlankKeyIsRejected() throws HttpException, IOException {
        stubGetRequestURI(mockRequest, "/");

        BaseHttpMethodHandler handler = createHttpGetMethodHandler();

        handler.handle(mockRequest, mockResponse, mockHttpContext);
    }

}
