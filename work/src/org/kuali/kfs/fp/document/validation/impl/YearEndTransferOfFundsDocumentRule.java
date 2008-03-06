/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.financial.rules;

import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.module.financial.document.TransferOfFundsDocument;
import org.kuali.module.financial.document.YearEndDocumentUtil;

/**
 * Business rule(s) applicable to <code>YearEndTransferOfFundsDocument</code>s
 * 
 * @see org.kuali.module.financial.rules.TransferOfFundsDocumentRule
 */
public class YearEndTransferOfFundsDocumentRule extends TransferOfFundsDocumentRule {

    /**
     * Overriding to return corresponding parent class TransferOfFunds.
     * 
     * @param financialDocument The financial document the class will be determined for.
     * @return The class type of the document passed in.
     * 
     * @see org.kuali.kfs.rules.AccountingDocumentRuleBase#getAccountingLineDocumentClass(org.kuali.kfs.document.AccountingDocument)
     */
    @Override
    protected Class getAccountingLineDocumentClass(AccountingDocument financialDocument) {
        return TransferOfFundsDocument.class;
    }


}