/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
