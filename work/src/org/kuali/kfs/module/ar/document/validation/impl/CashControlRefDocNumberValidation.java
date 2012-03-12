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
