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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

public class RequisitionAccountClosedValidation extends GenericValidation {

    AccountingLine accountingLine;
    
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        
        accountingLine.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
        if (accountingLine.getAccount() != null && !accountingLine.getAccount().isActive()) {
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.ACCOUNT_NUMBER, PurapKeyConstants.ERROR_REQUISITION_ACCOUNT_CLOSED, accountingLine.getChartOfAccountsCode(), accountingLine.getAccountNumber());

            valid &= false;
        }

        return valid;
    }

    public AccountingLine getAccountingLine() {
        return accountingLine;
    }

    public void setAccountingLine(AccountingLine accountingLine) {
        this.accountingLine = accountingLine;
    }

}
