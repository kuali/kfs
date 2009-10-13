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

import java.util.Collection;

import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.Transaction;

/**
 * This interface declares methods needed for posting transactions to the appropriate balance records.
 */
public interface BalanceCalculator {
    /**
     * Given a collection of balance records, returns the balance that the given transaction would post to
     * or creates a new balance record
     * 
     * @param balanceList a Collection of balance records
     * @param t the transaction to post
     * @return the balance to post against
     */
    public Balance findBalance(Collection balanceList, Transaction t);

    /**
     * Updates the balance based on the given transaction
     * 
     * @param t the transaction to post
     * @param b the balance being posted against
     */
    public void updateBalance(Transaction t, Balance b);
}
