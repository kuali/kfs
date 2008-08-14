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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.math.BigDecimal;

import org.kuali.kfs.module.purap.document.AccountsPayableDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.TransactionalDocument;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * Business rule(s) applicable to Payment Request documents.
 */
public class ElectronicInvoiceRejectDocumentRule extends AccountsPayableDocumentRuleBase {

    private static KualiDecimal zero = KualiDecimal.ZERO;
    private static BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    /**
     * Tabs included on Payment Request Documents are: Invoice
     * 
     * @see org.kuali.kfs.module.purap.document.validation.impl.PurchasingAccountsPayableDocumentRuleBase#processValidation(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public boolean processValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = true; //super.processValidation(purapDocument);
        return valid;
    }

    /**
     * @see org.kuali.rice.kns.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = true;
        return isValid;
    }


    /**
     * Performs item validations for the rules that are only applicable to Payment Request Document. In EPIC, we are
     * also doing similar steps as in this method within the validateFormatter, which is called upon Save. Therefore now we're also
     * calling the same validations upon Save.
     * 
     * @param purapDocument - purchasing accounts payable document
     * @return
     */
    @Override
    public boolean processItemValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = true;
//        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) purapDocument;
//        for (PurApItem item : purapDocument.getItems()) {
//            PaymentRequestItem preqItem = (PaymentRequestItem) item;
//            valid &= validateEachItem(paymentRequestDocument, preqItem);
//        }
        return valid;
    }    

    public boolean processContinuePurapBusinessRules(TransactionalDocument document) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean processCalculateAccountsPayableBusinessRules(AccountsPayableDocument document) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean processPreCalculateAccountsPayableBusinessRules(AccountsPayableDocument document) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean processCancelAccountsPayableBusinessRules(AccountsPayableDocument document) {
        // TODO Auto-generated method stub
        return false;
    }
}
