package org.hydracache.server.httpd.handler;

import java.io.IOException;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.hydracache.data.hashing.HashFunction;
import org.hydracache.data.hashing.KetamaBasedHashFunction;
import org.hydracache.server.Identity;
import org.hydracache.server.IdentityMarshaller;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.data.versioning.IncrementVersionFactory;
import org.hydracache.server.harmony.storage.HarmonyDataBank;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;

public class AbstractHttpMethodHandlerTest {

    protected Mockery context;
    protected HttpResponse response;
    protected IncrementVersionFactory versionFactoryMarshaller;
    protected HashFunction hashFunction = new KetamaBasedHashFunction();
    protected Long testKey = 1234L;
    protected Identity sourceId = new Identity(70);
    protected Identity localId = new Identity(71);
        

    public AbstractHttpMethodHandlerTest() {
        super();
    }

    @Before
    public void initialize() {
        context = new Mockery() {
            {
                setImposteriser(ClassImposteriser.INSTANCE);
            }
        };

        response = context.mock(HttpResponse.class);

        versionFactoryMarshaller = new IncrementVersionFactory();
        versionFactoryMarshaller
                .setIdentityMarshaller(new IdentityMarshaller());
    }

    @After
    public void after() {
        context.assertIsSatisfied();
    }

    protected void addNullReturnedFromLocalGetExp(final HarmonyDataBank dataBank)
            throws IOException {
        context.checking(new Expectations() {
            {
                atLeast(1).of(dataBank).getLocally(with(testKey));
                will(returnValue(null));
            }
        });
    }

    protected void addSuccessLocalPutExp(final HarmonyDataBank dataBank)
            throws Exception {
        context.checking(new Expectations() {
            {
                one(dataBank).put(with(any(Data.class)));
            }
        });
    }

    protected void addSetCreatedStatusCodeExp(final HttpResponse response) {
        context.checking(new Expectations() {
            {
                one(response).setStatusCode(HttpStatus.SC_CREATED);
            }
        });
    }

    protected void addSetBinaryDataEntityExp(final HttpResponse response) {
        context.checking(new Expectations() {
            {
                one(response).setEntity(with(any(ByteArrayEntity.class)));
            }
        });
    }

    protected byte[] generateRandomBytes() {
        return RandomStringUtils.random(10).getBytes();
    }

    protected void addConflictLocalGetExp(final HarmonyDataBank dataBank)
            throws IOException {
        context.checking(new Expectations() {
            {
                one(dataBank).getLocally(with(any(Long.class)));
                Data data = new Data();
                data.setVersion(new IncrementVersionFactory().create(sourceId)
                        .incrementFor(localId));
                will(returnValue(data));
            }
        });
    }

    protected void addSetConflictStatusCodeExp(final HttpResponse response) {
        context.checking(new Expectations() {
            {
                one(response).setStatusCode(HttpStatus.SC_CONFLICT);
            }
        });
    }

    protected void addSetStringMessageEntityExp(final HttpResponse response) {
        context.checking(new Expectations() {
            {
                one(response).setEntity(with(any(StringEntity.class)));
            }
        });
    }

    protected void addExistingDataReturnedFromLocalGetExp(
            final HarmonyDataBank dataBank) throws IOException {
        context.checking(new Expectations() {
            {
                atLeast(1).of(dataBank).getLocally(with(any(Long.class)));
                Data data = new Data();
                data.setVersion(new IncrementVersionFactory().create(sourceId));
                will(returnValue(data));
            }
        });
    }

    protected void addSetOkStatusCodeExp(final HttpResponse response) {
        context.checking(new Expectations() {
            {
                one(response).setStatusCode(HttpStatus.SC_OK);
            }
        });
    }

}