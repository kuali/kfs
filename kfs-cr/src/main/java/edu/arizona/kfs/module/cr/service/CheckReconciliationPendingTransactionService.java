package edu.arizona.kfs.module.cr.service;

import org.kuali.kfs.pdp.businessobject.PaymentGroup;

/**
 * Check Reconciliation Pending Transaction Service
 */
public interface CheckReconciliationPendingTransactionService {

    /**
     * Generates the appropriate Pending Transactions for a Stop Payment.
     */
    public void generatePendingTransactionStop(PaymentGroup paymentGroup);

    /**
     * Generates the appropriate Pending Transactions for a Cancelled Payment.
     */
    public void generatePendingTransactionCancel(PaymentGroup paymentGroup);

    /**
     * Generates the appropriate Pending Transactions for a Stale Payment.
     */
    public void generatePendingTransactionStale(PaymentGroup paymentGroup);

}
