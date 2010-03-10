package org.hydracache.server.harmony.handler;

import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.hydracache.concurrent.SimpleResultFuture;
import org.hydracache.protocol.control.message.ControlMessage;
import org.hydracache.protocol.control.message.ResponseMessage;
import org.hydracache.server.harmony.util.RequestRegistry;

public class ResponseHandler implements ControlMessageHandler {
    private static Logger log = Logger.getLogger(ResponseHandler.class);

    private RequestRegistry requestRegistry;

    public ResponseHandler(RequestRegistry requestRegistry) {
        this.requestRegistry = requestRegistry;
    }

    @Override
    public void handle(ControlMessage message) throws Exception {
        Validate.isTrue(message instanceof ResponseMessage,
                "Unsupported message[" + message + "] received");

        ResponseMessage responseMsg = (ResponseMessage) message;

        UUID requestId = responseMsg.getReplyToId();

        if (requestId == null) {
            if (log.isDebugEnabled())
                log.debug("Ignoring response message with missing replyTo: "
                        + message);
            return;
        }

        SimpleResultFuture<ResponseMessage> requestResultFuture = requestRegistry
                .retrieveResultFuture(requestId);

        if (requestResultFuture == null) {
            if (log.isDebugEnabled())
                log.debug("Ignoring unrelated response message: " + message);
            return;
        }

        requestResultFuture.add(responseMsg);

        if (log.isDebugEnabled())
            log.debug("Successfully processed response message: " + message);
    }

    protected boolean messageIsNotFromOurNeighbor(ControlMessage message) {
        return false;
    }

}
