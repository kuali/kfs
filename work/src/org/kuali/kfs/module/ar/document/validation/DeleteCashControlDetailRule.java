/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.ar.rule;

import org.kuali.core.document.TransactionalDocument;
import org.kuali.module.ar.bo.CashControlDetail;

public interface DeleteCashControlDetailRule <F extends TransactionalDocument > extends CashControlDetailRule {


    /**
     * This method is called when a cash control detail is deleted
     * @param transactionalDocument the cash control document
     * @param cashControlDetail the cash control detail being deleted
     * @return true if allowed to delete, false otherwise
     */
    public boolean processDeleteCashControlDetailBusinessRules(F transactionalDocument, CashControlDetail cashControlDetail);
}
