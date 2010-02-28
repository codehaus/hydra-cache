package org.hydracache.server.harmony.health;

import org.hydracache.protocol.control.message.RequestMessage;
import org.hydracache.server.harmony.core.Node;

public class HeartBeat extends RequestMessage {

    private static final long serialVersionUID = 1L;

    private Node sourceNode;

    public HeartBeat(Node sourceNode) {
        super(sourceNode.getId());

        this.sourceNode = sourceNode;
    }

    public Node getSourceNode() {
        return sourceNode;
    }

}
