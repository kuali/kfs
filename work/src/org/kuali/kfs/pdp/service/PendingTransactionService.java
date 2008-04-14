/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
 */
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
