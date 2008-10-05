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
package org.kuali.kfs.module.ld.document.datadictionary;

import java.util.Arrays;
import java.util.List;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.FinancialSystemUser;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase;

/**
 * Data dictionary definition that includes metadata for an accounting document about one of its groups of accounting lines (typically source vs. target, but this should open things up).
 */
public class SalaryExpenseTransferAccountingLineAuthorizerBase extends AccountingLineAuthorizerBase {
  
    /**
     * Overrides the method in class AccountingLineAuthorizerBase to return false to make the line readonly
     * 
     * If the account does not exist then it's editable (so the current user can correct it!!) or if the user has responsiblity on the account, it's editable; it's not editable otherwise
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizer#isAccountLineEditable(org.kuali.kfs.sys.businessobject.AccountingLine, org.kuali.kfs.sys.businessobject.FinancialSystemUser)
     */
    @Override
    public boolean isAccountLineEditable(AccountingDocument document, AccountingLine accountingLine, FinancialSystemUser currentUser, String editModeForAccountingLine) {
        return false; // default?  you can't edit the line none!
    }    
}
