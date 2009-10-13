/*
 * Copyright 2007 The Kuali Foundation
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
/*
 * Created on Sep 2, 2004
 *
 */
package org.kuali.kfs.pdp.dataaccess;

import java.util.Iterator;

import org.kuali.kfs.pdp.businessobject.GlPendingTransaction;

/**
 * DAO Interface for <code>GlPendingTransaction</code>
 */
public interface PendingTransactionDao {

    /**
     * Get all of the GL transactions where the extract flag is null
     * 
     * @return Iterator of all the transactions
     */
    public Iterator<GlPendingTransaction> getUnextractedTransactions();
    
    /**
     * Deletes all transaction records where the process indicator is true
     */
    public void clearExtractedTransactions();
}
