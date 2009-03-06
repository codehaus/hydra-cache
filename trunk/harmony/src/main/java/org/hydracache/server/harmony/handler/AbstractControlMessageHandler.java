package org.hydracache.server.harmony.handler;

import org.apache.log4j.Logger;
import org.hydracache.protocol.control.message.ControlMessage;
import org.hydracache.server.harmony.core.Space;

public abstract class AbstractControlMessageHandler implements
        ControlMessageHandler {
    private static Logger log = Logger
            .getLogger(AbstractControlMessageHandler.class);

    protected Space space;

    public AbstractControlMessageHandler(Space space) {
        this.space = space;
    }

    protected boolean messageIsNotFromOurNeighbor(ControlMessage message) {
        return !space.findSubstancesForLocalNode().isNeighbor(
                message.getSource());
    }

    @Override
    public void handle(ControlMessage message) throws Exception {
        if (messageIsNotFromOurNeighbor(message)) {
            log.debug("Discarding message[" + message + "] from stranger");
            return;
        }

        doHandle(message);
    }

    protected abstract void doHandle(ControlMessage message) throws Exception;

}