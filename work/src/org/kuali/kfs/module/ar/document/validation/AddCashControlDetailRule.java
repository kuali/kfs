/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.validation;

import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.rice.krad.document.TransactionalDocument;

/**
 * Rule invoked when a new customer invoice detail is added
 */
public interface AddCashControlDetailRule<F extends TransactionalDocument > extends CashControlDetailRule {
    

    /**
     * This method is called when a cash control detail is added
     * @param transactionalDocument the cash control document
     * @param cashControlDetail the detail to be added
     * @return true if valid to be added, false otherwise
     */
    public boolean processAddCashControlDetailBusinessRules(F transactionalDocument, CashControlDetail cashControlDetail);

}
