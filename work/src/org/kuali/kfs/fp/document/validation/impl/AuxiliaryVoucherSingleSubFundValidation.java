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
package org.kuali.kfs.fp.document.validation.impl;

import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validation for Auxiliary Voucher that checks that all the source accounting lines on the document use
 * only one sub fund group among them.
 */
public class AuxiliaryVoucherSingleSubFundValidation extends GenericValidation {
    private AccountingDocument accountingDocumentForValidation;

    /**
     * Iterates <code>{@link AccountingLine}</code> instances in a given <code>{@link FinancialDocument}</code> instance and
     * compares them to see if they are all in the same Sub-Fund Group.
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;

        String baseSubFundGroupCode = null;
        int index = 0;

        List<AccountingLine> lines = getAccountingDocumentForValidation().getSourceAccountingLines();
        for (AccountingLine line : lines) {
            if (index == 0) {
                baseSubFundGroupCode = line.getAccount().getSubFundGroupCode();
            }
            else {
                String currentSubFundGroup = line.getAccount().getSubFundGroupCode();
                if (!currentSubFundGroup.equals(baseSubFundGroupCode)) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.ACCOUNTING_LINE_ERRORS, KFSKeyConstants.AuxiliaryVoucher.ERROR_DIFFERENT_SUB_FUND_GROUPS);
                    return false;
                }
            }
            index++;
        }
        return true;
    }

    /**
     * Gets the accountingDocumentForValidation attribute. 
     * @return Returns the accountingDocumentForValidation.
     */
    public AccountingDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }
}
