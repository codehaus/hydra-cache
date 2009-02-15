package org.hydracache.server.harmony.handler;

import org.hydracache.protocol.control.message.ControlMessage;

public interface ControlMessageHandler {

    void handle(ControlMessage message) throws Exception;

}