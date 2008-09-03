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
package org.kuali.kfs.fp.document.validation.impl;

import org.kuali.kfs.fp.document.InternalBillingDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBase;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * Business rule(s) applicable to InternalBilling document.
 */
public class InternalBillingDocumentRule extends AccountingDocumentRuleBase {

    /**
     * Overrides to only disallow zero, allowing negative amounts.  
     * 
     * @param document The document which contains the accounting line being validated.
     * @param accountingLine The accounting line containing the amount being validated.
     * @return True if the amount is not zero, false otherwise.
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#isAmountValid(FinancialDocument, AccountingLine)
     */
    @Override
    public boolean isAmountValid(AccountingDocument document, AccountingLine accountingLine) {
        if (accountingLine.getAmount().equals(KualiDecimal.ZERO)) {
            GlobalVariables.getErrorMap().putError(KFSConstants.AMOUNT_PROPERTY_NAME, KFSKeyConstants.ERROR_ZERO_AMOUNT, "an accounting line");
            LOG.info("failing isAmountValid - zero check");
            return false;
        }
        return true;
    }

    /**
     * This method overrides the processCustomRouteDocumentBusinessRules() method in AccountingDocumentRuleBase to 
     * allow for additional rules to be run prior to routing.  In addition to calling the parent method to perform the 
     * general business rule checks, this method also performs a validation check on all InternalBillingItems associated 
     * with the given document.  
     * 
     * @param document The document being routed.
     * @return True if the parent method finds no business rule problems and all associated InternalBillingItems are valid.
     * 
     * @see FinancialDocumentRuleBase#processCustomRouteDocumentBusinessRules(Document)
     */
    @Override
    public boolean processCustomRouteDocumentBusinessRules(Document document) {
        // This super method actually does something.
        boolean success = true;
        success &= super.processCustomRouteDocumentBusinessRules(document);
        if (success) {
            success &= validateItems((InternalBillingDocument) document);
        }
        // TODO: for phase II, when capital object codes are allowed on expense accounting lines, check that there are any if and
        // only if the Capital Assets tab contains information about the associated capital asset.
        // TODO: for phase II, check that this bills for no more than one capital asset.
        return success;
    }

    /**
     * Validates all the InternalBillingItems in the given Document, adding global errors for invalid items. It just uses the
     * DataDictionary validation.
     * 
     * @param internalBillingDocument The document the InternalBillingItems will be retrieved from to validate.
     * @return Whether or not any associated items within the given document are invalid.
     */
    private boolean validateItems(InternalBillingDocument internalBillingDocument) {
        boolean retval = true;
        for (int i = 0; i < internalBillingDocument.getItems().size(); i++) {
            String propertyName = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSPropertyConstants.ITEM + "[" + i + "]";
            retval &= getDictionaryValidationService().isBusinessObjectValid(internalBillingDocument.getItem(i), propertyName);
        }
        return retval;
    }
}
