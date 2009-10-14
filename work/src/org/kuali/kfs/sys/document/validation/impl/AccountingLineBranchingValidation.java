/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.sys.document.validation.impl;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.validation.BranchingValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

public class AccountingLineBranchingValidation extends BranchingValidation {
    
    public static final String IS_NEW_LINE = "isNewLine";
    public static final String IS_OLD_LINE = "isOldLine";
    
    protected AccountingLine accountingLineForValidation;

    @Override
    protected String determineBranch(AttributedDocumentEvent event) {
        if (accountingLineForValidation.getSequenceNumber() != null) {
            return IS_OLD_LINE;
        } else if (accountingLineForValidation.getSequenceNumber() == null) {
           return IS_NEW_LINE;
        }
        return null;
    }

    /**
     * Gets the accountingLineForValidation attribute. 
     * @return Returns the accountingLineForValidation.
     */
    public AccountingLine getAccountingLineForValidation() {
        return accountingLineForValidation;
    }

    /**
     * Sets the accountingLineForValidation attribute value.
     * @param accountingLineForValidation The accountingLineForValidation to set.
     */
    public void setAccountingLineForValidation(AccountingLine accountingLine) {
        this.accountingLineForValidation = accountingLine;
    }
    
    
}
