package org.kuali.module.purap.exceptions;

public class PurError extends Error {
    public PurError() {
        super();
    }

    public PurError(String message) {
        super(message);
    }

    public PurError(String message, Throwable arg1) {
        super(message, arg1);
    }

    public PurError(Throwable arg0) {
        super(arg0);
    }
}
