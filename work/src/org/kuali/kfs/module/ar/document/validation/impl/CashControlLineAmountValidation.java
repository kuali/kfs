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

import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

public class CashControlLineAmountValidation extends GenericValidation {

    private CashControlDocument cashControlDocument;
    private CashControlDetail cashControlDetail;
    
    public boolean validate(AttributedDocumentEvent event) {
        
        boolean isValid = true;

        // line amount cannot be zero
        if (cashControlDetail.getFinancialDocumentLineAmount().isZero()) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.CashControlDocumentFields.FINANCIAL_DOCUMENT_LINE_AMOUNT, ArKeyConstants.ERROR_LINE_AMOUNT_CANNOT_BE_ZERO);
            isValid = false;
        }
        // line amount cannot be negative
        if (cashControlDetail.getFinancialDocumentLineAmount().isNegative()) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.CashControlDocumentFields.FINANCIAL_DOCUMENT_LINE_AMOUNT, ArKeyConstants.ERROR_LINE_AMOUNT_CANNOT_BE_NEGATIVE);
            isValid = false;
        }
        return isValid;
    }

    public CashControlDocument getCashControlDocument() {
        return cashControlDocument;
    }

    public void setCashControlDocument(CashControlDocument cashControlDocument) {
        this.cashControlDocument = cashControlDocument;
    }

    public CashControlDetail getCashControlDetail() {
        return cashControlDetail;
    }

    public void setCashControlDetail(CashControlDetail cashControlDetail) {
        this.cashControlDetail = cashControlDetail;
    }
}
