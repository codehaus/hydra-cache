package org.hydracache.server.harmony.handler;

import java.util.UUID;

import org.hydracache.protocol.control.message.ResponseMessage;
import org.hydracache.server.Identity;
import org.hydracache.server.harmony.util.RequestRegistry;
import org.junit.Test;

public class ResponseHandlerTest {

    @Test
    public void ensureNonRegisteredResponseGetIgnored() throws Exception {
        ResponseHandler handler = new ResponseHandler(new RequestRegistry());

        Identity testSourceId = new Identity(8080);
        
        ResponseMessage responseMsg = new ResponseMessage(testSourceId, UUID.randomUUID());
        
        handler.handle(responseMsg);
    }
    
    @Test
    public void ensureNullRequestIdGetIgnored() throws Exception {
        ResponseHandler handler = new ResponseHandler(new RequestRegistry());

        Identity testSourceId = new Identity(8080);
        
        handler.handle(new ResponseMessage(testSourceId, UUID.randomUUID()));
    }

}
