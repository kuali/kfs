package edu.arizona.kfs.module.cr.exception;

import edu.arizona.kfs.module.cr.businessobject.CheckReconciliation;

/**
 * Special Exception for handling when a Bank account is not found in the database.
 */
public class BankNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -1212031288870751575L;

    private CheckReconciliation errorCheckReconciliation;

    public BankNotFoundException() {
        super();
    }

    public BankNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public BankNotFoundException(String message) {
        super(message);
    }

    public BankNotFoundException(Throwable cause) {
        super(cause);
    }

    public CheckReconciliation getErrorCheckReconciliation() {
        return errorCheckReconciliation;
    }

    public void setErrorCheckReconciliation(CheckReconciliation errorCheckReconciliation) {
        this.errorCheckReconciliation = errorCheckReconciliation;
    }

}
