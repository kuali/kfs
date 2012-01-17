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
package org.kuali.kfs.fp.document.validation.event;

import org.kuali.kfs.fp.businessobject.CashDrawer;
import org.kuali.kfs.fp.businessobject.CashieringTransaction;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;

/**
 * An event generated when a cash management document is about to apply a cashiering transaction
 */
public interface CashieringTransactionApplicationEvent extends KualiDocumentEvent {
    
    /**
     * Returns the cashiering transaction to be validated
     * @return the cashiering transaction to be validated
     */
    public abstract CashieringTransaction getCashieringTransaction();
    
    /**
     * Returns the cash drawer that the cashiering transaction to validate will be applied to
     * @return the cash drawer's current state on the cash management document initiating the cashiering transaction
     */
    public abstract CashDrawer getCashDrawer();
}
