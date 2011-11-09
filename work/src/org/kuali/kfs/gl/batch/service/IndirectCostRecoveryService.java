/*
 * Copyright 2006 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.gl.batch.service;

import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.sys.service.ReportWriterService;

/**
 * An interface with a predicate that tells if a transaction with the given fields would be an ICR transaction or not
 */
public interface IndirectCostRecoveryService {
    /**
     * This will determine if this transaction is an ICR eligible transaction
     * 
     * @param transaction the transaction which is being determined to be ICR or not
     * @param objectType the object type of the transaction
     * @param account the account of the transaction
     * @param objectCode the object code of the transaction
     * @return true if the transaction is an ICR transaction and therefore should have an expenditure transaction created for it; false if otherwise
     */
    public abstract boolean isIcrTransaction(Transaction transaction, ReportWriterService reportWriterService);
}
