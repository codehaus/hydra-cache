package org.hydracache.protocol.control.message;

import java.util.UUID;

import org.hydracache.server.Identity;

public class VersionConflictRejection extends ResponseMessage {

    private static final long serialVersionUID = 1L;

    public VersionConflictRejection(Identity source, UUID replyToId) {
        super(source, replyToId);
    }
}
