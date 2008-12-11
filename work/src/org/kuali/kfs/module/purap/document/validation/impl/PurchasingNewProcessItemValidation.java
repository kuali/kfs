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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.util.List;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.util.GlobalVariables;

public class PurchasingNewProcessItemValidation extends PurchasingAccountsPayableNewProcessItemValidation {

    private PurapService purapService;
    
    public boolean validate(AttributedDocumentEvent event) {
        return super.validate(event);
    }
        
    /**
     * Overrides the method in PurchasingAccountsPayableDocumentRuleBase to also invoke the validateAccountNotExpired for each of
     * the accounts.
     * 
     * @see org.kuali.kfs.module.purap.document.validation.impl.PurchasingAccountsPayableDocumentRuleBase#processAccountValidation(org.kuali.kfs.sys.document.AccountingDocument,
     *      java.util.List, java.lang.String)
     */
    @Override
    public boolean processAccountValidation(AccountingDocument accountingDocument, List<PurApAccountingLine> purAccounts, String itemLineNumber) {
        boolean valid = true;
        for (PurApAccountingLine accountingLine : purAccounts) {
            boolean notExpired = this.validateAccountNotExpired(accountingLine);
            if (!notExpired) {
                GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_ACCOUNT_EXPIRED, itemLineNumber + " has ", accountingLine.getAccount().getAccountNumber());
            }
        }
        valid &= super.processAccountValidation(accountingDocument, purAccounts, itemLineNumber);

        return valid;
    }
    
    /**
     * Validates that the account is not expired.
     * 
     * @param accountingLine The account to be validated.
     * @return boolean false if the account is expired.
     */
    private boolean validateAccountNotExpired(AccountingLine accountingLine) {
        accountingLine.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
        if (accountingLine.getAccount() != null && accountingLine.getAccount().isExpired()) {

            return false;
        }

        return true;
    }
    
    public PurapService getPurapService() {
        return purapService;
    }

    public void setPurapService(PurapService purapService) {
        this.purapService = purapService;
    }    

}
