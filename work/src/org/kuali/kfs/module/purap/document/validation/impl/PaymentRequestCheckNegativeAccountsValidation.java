/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.util.List;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.util.GlobalVariables;

public class PaymentRequestCheckNegativeAccountsValidation extends GenericValidation {

    private PurapAccountingService purapAccountingService;
    
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument)event.getDocument();
        
        GlobalVariables.getErrorMap().clearErrorPath();
        //GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);

        // if this was set somewhere on the doc(for later use) in prepare for save we could avoid this call
        List<SourceAccountingLine> sourceLines = purapAccountingService.generateSummary(paymentRequestDocument.getItems());

        for (SourceAccountingLine sourceAccountingLine : sourceLines) {
            // check if the summary account is for tax withholding
            boolean isTaxAccount = purapAccountingService.isTaxAccount(paymentRequestDocument, sourceAccountingLine);
                        
            // exclude tax withholding accounts from non-negative requirement
            if (!isTaxAccount && sourceAccountingLine.getAmount().isNegative()) {
                String accountString = sourceAccountingLine.getChartOfAccountsCode() + " - " + sourceAccountingLine.getAccountNumber() + " - " + sourceAccountingLine.getFinancialObjectCode();
                GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ACCOUNT_AMOUNT_TOTAL, accountString, sourceAccountingLine.getAmount() + "");
                valid &= false;
            }
        }
        
        GlobalVariables.getErrorMap().clearErrorPath();
        return valid;
    }

    public PurapAccountingService getPurapAccountingService() {
        return purapAccountingService;
    }

    public void setPurapAccountingService(PurapAccountingService purapAccountingService) {
        this.purapAccountingService = purapAccountingService;
    }

}
