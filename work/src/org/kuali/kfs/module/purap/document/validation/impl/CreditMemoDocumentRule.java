/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.purap.rules;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionMessage;
import org.kuali.core.document.Document;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;

public class CreditMemoDocumentRule extends AccountsPayableDocumentRuleBase {

    /**
     * Tabs included on Payment Request Documents are:
     *   Credit Memo
     * 
     * @see org.kuali.module.purap.rules.PurchasingAccountsPayableDocumentRuleBase#processValidation(org.kuali.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public boolean processValidation(PurchasingAccountsPayableDocument purapDocument) {
      //  boolean valid = super.processValidation(purapDocument);
        boolean valid = processCreditMemoValidation((CreditMemoDocument)purapDocument);
        return valid;
    }
    
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = true;
        CreditMemoDocument  creditMemoDocument = (CreditMemoDocument) document;
        return isValid &= processValidation(creditMemoDocument);
    }
    
    /**
     * This method performs any validation for the Credit Memo tab.
     * 
     * @param cmDocument
     * @return
     */
    public boolean processCreditMemoValidation(CreditMemoDocument cmDocument) {
        boolean valid = true;
        //TODO code validation here
        valid &= validateCreditMemoInitTab(cmDocument);
        return valid;
    }
    
    /**
     * This method performs  validation for the Credit Memo Init tab.
     * 
     * @param cmDocument
     * @return
     */
    public boolean validateCreditMemoInitTab(CreditMemoDocument cmDocument) {
        boolean valid = true;
        //TODO code validation here
        if(!(ObjectUtils.isNotNull(cmDocument.getPaymentRequestIdentifier()) ^ ObjectUtils.isNotNull(cmDocument.getVendorHeaderGeneratedIdentifier()) ^ 
                ObjectUtils.isNotNull(cmDocument.getPurchaseOrderIdentifier()))) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.CREDIT_MEMO_INIT_REQUIRED_FIELDS, PurapKeyConstants.ERROR_CREDIT_MEMO_REQUIRED_FIELDS);
               valid &= false;
           }
        /*
        if(!(StringUtils.isNotEmpty(cmDocument.getPaymentRequestIdentifier().toString()) ^ StringUtils.isNotEmpty(cmDocument.getVendorHeaderGeneratedIdentifier().toString()) ^ 
             StringUtils.isNotEmpty(cmDocument.getPurchaseOrderIdentifier().toString()))) {
             GlobalVariables.getErrorMap().putError(PurapPropertyConstants.CREDIT_MEMO_INIT_REQUIRED_FIELDS, PurapKeyConstants.ERROR_CREDIT_MEMO_REQUIRED_FIELDS);
            valid &= false;
        } */
        
        return valid;
    }
}
