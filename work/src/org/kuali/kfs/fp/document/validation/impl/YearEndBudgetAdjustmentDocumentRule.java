/*
 * Copyright 2006 The Kuali Foundation.
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

import static org.kuali.module.financial.rules.TransferOfFundsDocumentRuleConstants.YEAR_END_TRANSFER_OF_FUNDS_DOC_TYPE_CODE;

import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.module.financial.document.YearEndDocumentUtil;

/**
 * Business rules applicable to <code>YearEndBudgetAdjustmentDocument</code>
 */
public class YearEndBudgetAdjustmentDocumentRule extends BudgetAdjustmentDocumentRule {

    /**
     * year end document set:
     * <ol>
     * <li> the fiscal period code = 13
     * <li> fiscal year = previous fiscal year
     * </ol>
     * 
     * @see org.kuali.module.financial.rules.AccountingDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(org.kuali.core.document.AccountingDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    @Override
    protected void customizeExplicitGeneralLedgerPendingEntry(AccountingDocument accountingDocument, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry) {
        super.customizeExplicitGeneralLedgerPendingEntry(accountingDocument, accountingLine, explicitEntry);
        YearEndDocumentUtil.customizeExplicitGeneralLedgerPendingEntry(accountingDocument, accountingLine, explicitEntry);
    }

    
    
    @Override
    protected boolean customizeOffsetGeneralLedgerPendingEntry(AccountingDocument accountingDocument, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        boolean success = super.customizeOffsetGeneralLedgerPendingEntry(accountingDocument, accountingLine, explicitEntry, offsetEntry);
        YearEndDocumentUtil.customizeExplicitGeneralLedgerPendingEntry(accountingDocument, accountingLine, explicitEntry);
        return success;
    }



    /**
     * @see org.kuali.module.financial.rules.BudgetAdjustmentDocumentRule#getTransferDocumentType()
     */
    @Override
    protected String getTransferDocumentType() {
        return YEAR_END_TRANSFER_OF_FUNDS_DOC_TYPE_CODE;
    }


}
