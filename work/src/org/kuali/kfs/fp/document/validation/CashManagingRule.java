/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.fp.document.validation;

import org.kuali.kfs.fp.businessobject.CashDrawer;
import org.kuali.kfs.fp.businessobject.CashieringTransaction;

/**
 * Callbacks for rules that the Cash Management document checks
 */
public interface CashManagingRule {
    
    /**
     * Validates a cashiering transaction before it is added to a document
     * @param cashDrawer the cash drawer the transaction will be applied to
     * @param cashieringTransaction the cashering transaction which may be applied
     * @return true if the transaction would be valid, false otherwise
     */
    public abstract boolean processCashieringTransactionApplication(CashDrawer cashDrawer, CashieringTransaction cashieringTransaction);
}
