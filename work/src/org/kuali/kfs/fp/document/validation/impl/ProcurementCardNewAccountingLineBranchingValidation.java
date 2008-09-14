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
package org.kuali.kfs.fp.document.validation.impl;

import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.validation.BranchingValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

public class ProcurementCardNewAccountingLineBranchingValidation extends BranchingValidation {
    
    public static final String IS_NEW_LINE = "isNewLine";
    public static final String IS_OLD_LINE = "isOldLine";
    
    private AccountingLine accountingLine;

    @Override
    protected String determineBranch(AttributedDocumentEvent event) {
        if (accountingLine.getSequenceNumber() != null) {
            return IS_OLD_LINE;
        } else if (accountingLine.getSequenceNumber() == null) {
           return IS_NEW_LINE;
        }
        return null;
    }

    /**
     * Gets the accountingLine attribute. 
     * @return Returns the accountingLine.
     */
    public AccountingLine getAccountingLine() {
        return accountingLine;
    }

    /**
     * Sets the accountingLine attribute value.
     * @param accountingLine The accountingLine to set.
     */
    public void setAccountingLine(AccountingLine accountingLine) {
        this.accountingLine = accountingLine;
    }
    
    
}
