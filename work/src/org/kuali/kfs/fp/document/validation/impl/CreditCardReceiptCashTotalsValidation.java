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

import static org.kuali.kfs.fp.document.validation.impl.CreditCardReceiptDocumentRuleConstants.CREDIT_CARD_RECEIPT_PREFIX;
import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

import org.kuali.kfs.fp.document.CreditCardReceiptDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSKeyConstants.CashReceipt;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This class...
 */
public class CreditCardReceiptCashTotalsValidation extends GenericValidation {
    private CreditCardReceiptDocument accountingDocumentForValidation;
    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        CreditCardReceiptDocument ccrDocument = getAccountingDocumentForValidation();
        KualiDecimal totalAmount = ccrDocument.getTotalDollarAmount();
        String propertyName = KFSPropertyConstants.CREDIT_CARD_RECEIPTS_TOTAL;
        String documentEntryName = ccrDocument.getDocumentHeader().getWorkflowDocument().getDocumentTypeName();
        
        boolean isValid = true;
        String errorProperty = CREDIT_CARD_RECEIPT_PREFIX + propertyName;

        // treating null totalAmount as if it were a zero
        DataDictionaryService dds = SpringContext.getBean(DataDictionaryService.class);
        String errorLabel = dds.getAttributeLabel(documentEntryName, propertyName);
        if ((totalAmount == null) || totalAmount.isZero()) {
            GlobalVariables.getMessageMap().putError(errorProperty, CashReceipt.ERROR_ZERO_TOTAL, errorLabel);

            isValid = false;
        }
        else {
            int precount = GlobalVariables.getMessageMap().getNumberOfPropertiesWithErrors();

            DictionaryValidationService dvs = SpringContext.getBean(DictionaryValidationService.class);
            dvs.validateDocumentAttribute(ccrDocument, propertyName, DOCUMENT_ERROR_PREFIX);

            // replace generic error message, if any, with something more readable
            GlobalVariables.getMessageMap().replaceError(errorProperty, KFSKeyConstants.ERROR_MAX_LENGTH, CashReceipt.ERROR_EXCESSIVE_TOTAL, errorLabel);

            int postcount = GlobalVariables.getMessageMap().getNumberOfPropertiesWithErrors();
            isValid = (postcount == precount);
        }

        return isValid;
    }
    /**
     * Gets the documentForValidation attribute. 
     * @return Returns the documentForValidation.
     */
    public CreditCardReceiptDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }
    /**
     * Sets the documentForValidation attribute value.
     * @param documentForValidation The documentForValidation to set.
     */
    public void setAccountingDocumentForValidation(CreditCardReceiptDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }

}
