package de.unibi.agbi.biodwh2.core.exceptions;

public class ReconException extends Exception {
    private static final long serialVersionUID = 8617788206805618787L;

    public ReconException() {
        super();
    }

    public ReconException(final String message) {
        super(message);
    }

    public ReconException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ReconException(final Throwable cause) {
        super(cause);
    }

    public ReconException(final String message, final Throwable cause, final boolean enableSuppression,
                          final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
