package org.hydracache.server.harmony.util;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.hydracache.concurrent.SimpleResultFuture;
import org.hydracache.protocol.control.message.ResponseMessage;

public class RequestRegistry {
    private ConcurrentHashMap<UUID, SimpleResultFuture<ResponseMessage>> requestResultMap = new ConcurrentHashMap<UUID, SimpleResultFuture<ResponseMessage>>();

    public void register(UUID requestId,
            SimpleResultFuture<ResponseMessage> resultFuture) {
        requestResultMap.put(requestId, resultFuture);
    }

    public SimpleResultFuture<ResponseMessage> retrieveResultFuture(
            UUID requestId) {
        return requestResultMap.get(requestId);
    }

}
