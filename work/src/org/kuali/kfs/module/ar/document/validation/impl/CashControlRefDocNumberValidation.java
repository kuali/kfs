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
package org.kuali.kfs.module.ar.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;

public class CashControlRefDocNumberValidation extends GenericValidation {

    private CashControlDocument cashControlDocument;
    private DocumentService documentService;
    
    public boolean validate(AttributedDocumentEvent event) {
        
        boolean isValid = true;
        GlobalVariables.getMessageMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        String paymentMedium = cashControlDocument.getCustomerPaymentMediumCode();
        if (ArConstants.PaymentMediumCode.CASH.equalsIgnoreCase(paymentMedium)) {
            String refDocNumber = cashControlDocument.getReferenceFinancialDocumentNumber();
            try {
                Long.parseLong(refDocNumber);
                if (StringUtils.isBlank(refDocNumber)) {
                    GlobalVariables.getMessageMap().putError(ArPropertyConstants.CashControlDocumentFields.REFERENCE_FINANCIAL_DOC_NBR, ArKeyConstants.ERROR_REFERENCE_DOC_NUMBER_CANNOT_BE_NULL_FOR_PAYMENT_MEDIUM_CASH);
                    isValid = false;
                }
                else {
                    boolean docExists = SpringContext.getBean(DocumentService.class).documentExists(refDocNumber);
                    if (!docExists) {
                        GlobalVariables.getMessageMap().putError(ArPropertyConstants.CashControlDocumentFields.REFERENCE_FINANCIAL_DOC_NBR, ArKeyConstants.ERROR_REFERENCE_DOC_NUMBER_MUST_BE_VALID_FOR_PAYMENT_MEDIUM_CASH);
                        isValid = false;
                    }
                }
            }
            catch (NumberFormatException nfe) {
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.CashControlDocumentFields.REFERENCE_FINANCIAL_DOC_NBR, ArKeyConstants.ERROR_REFERENCE_DOC_NUMBER_MUST_BE_VALID_FOR_PAYMENT_MEDIUM_CASH);
                isValid = false;
            }

        }
        GlobalVariables.getMessageMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        return isValid;
    }

    public CashControlDocument getCashControlDocument() {
        return cashControlDocument;
    }

    public void setCashControlDocument(CashControlDocument cashControlDocument) {
        this.cashControlDocument = cashControlDocument;
    }

    public DocumentService getDocumentService() {
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
}
