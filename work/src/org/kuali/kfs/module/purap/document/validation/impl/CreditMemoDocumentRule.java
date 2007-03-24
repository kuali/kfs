/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.purap.rules;

import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;

public class CreditMemoDocumentRule extends AccountsPayableDocumentRuleBase {

    /**
     * Tabs included on Payment Request Documents are:
     *   Credit Memo
     * 
     * @see org.kuali.module.purap.rules.PurchasingAccountsPayableDocumentRuleBase#processValidation(org.kuali.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public boolean processValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processValidation(purapDocument);
        valid &= processCreditMemoValidation((CreditMemoDocument)purapDocument);
        return valid;
    }
    

    /**
     * This method performs any validation for the Credit Memo tab.
     * 
     * @param cmDocument
     * @return
     */
    public boolean processCreditMemoValidation(CreditMemoDocument cmDocument) {
        boolean valid = true;
        //TODO code validation here
        return valid;
    }
}
