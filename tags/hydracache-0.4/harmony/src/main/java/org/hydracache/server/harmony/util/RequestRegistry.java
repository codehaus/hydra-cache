package org.hydracache.server.harmony.util;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import org.hydracache.concurrent.SimpleResultFuture;
import org.hydracache.protocol.control.message.ResponseMessage;

import edu.emory.mathcs.backport.java.util.Collections;

@SuppressWarnings("unchecked")
public class RequestRegistry {
    private Map<UUID, SimpleResultFuture<ResponseMessage>> requestResultMap = Collections
            .synchronizedMap(new WeakHashMap<UUID, SimpleResultFuture<ResponseMessage>>());

    public void register(UUID requestId,
            SimpleResultFuture<ResponseMessage> resultFuture) {
        requestResultMap.put(requestId, resultFuture);
    }

    public SimpleResultFuture<ResponseMessage> retrieveResultFuture(
            UUID requestId) {
        return requestResultMap.get(requestId);
    }

    boolean isEmpty() {
        return requestResultMap.isEmpty();
    }

}
