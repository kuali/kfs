/*
 * Created on Aug 30, 2004
 *
 */
package org.kuali.module.pdp.service;

import java.util.Iterator;

import org.kuali.module.pdp.bo.GlPendingTransaction;
import org.kuali.module.pdp.bo.PaymentDetail;
import org.kuali.module.pdp.bo.PaymentGroup;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author jsissom
 *
 */
@Transactional
public interface GlPendingTransactionService {
    public void createProcessPaymentTransaction(PaymentDetail pd, Boolean relieveLiabilities);
    public void createCancellationTransaction(PaymentGroup pg);
    public void createCancelReissueTransaction(PaymentGroup pg);

    /**
     * Save a transaction
     * 
     * @param tran
     */
    public void save(GlPendingTransaction tran);

    /**
     * Get all of the GL transactions where the extract flag is null
     * 
     * @return Iterator of all the transactions
     */
    public Iterator getUnextractedTransactions();
}
