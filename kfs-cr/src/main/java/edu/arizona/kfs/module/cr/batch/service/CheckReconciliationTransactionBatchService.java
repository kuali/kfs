package edu.arizona.kfs.module.cr.batch.service;

/**
 * Performs the work necessary for the CheckReconciliationTransactionStep.
 */
public interface CheckReconciliationTransactionBatchService {

    /**
     * Initializes objects inside the service that cannot be injected via Spring.
     */
    public boolean initializeServiceObjects();

    /**
     * Processes all Stopped payments.
     */
    public boolean processStoppedPayments();

    /**
     * Processes all Cancelled payments.
     */
    public boolean processCancelledPayments();

    /**
     * Processes all Stale payments.
     */
    public boolean processStalePayments();

    /**
     * Processes all Voided payments.
     */
    public boolean processVoidedPayments();

}
