/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.batch.service;

public interface CreateRecurringCashTransferTransactionsService {

    /**
     * Generates the necessary eDocs for the overnight investment of cash
     * (cash sweep) by generating asset increase eDocs for the KEMIDs based
     * upon the cash sweep model assigned to the KEMID record.
     * 
     * @return
     */
    public boolean createRecurringCashTransferTransactions();
}
