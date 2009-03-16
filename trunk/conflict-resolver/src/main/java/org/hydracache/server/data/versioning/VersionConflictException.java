package org.hydracache.server.data.versioning;

public class VersionConflictException extends Exception {

    private static final long serialVersionUID = 1L;

    public VersionConflictException() {
        super();
    }

    public VersionConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public VersionConflictException(String message) {
        super(message);
    }

    public VersionConflictException(Throwable cause) {
        super(cause);
    }

}
