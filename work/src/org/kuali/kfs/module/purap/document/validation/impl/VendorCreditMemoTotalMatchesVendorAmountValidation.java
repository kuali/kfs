/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.util.KNSGlobalVariables;

public class VendorCreditMemoTotalMatchesVendorAmountValidation extends GenericValidation {

    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        VendorCreditMemoDocument cmDocument = (VendorCreditMemoDocument)event.getDocument();
        
        // If UseTax is included, then the invoiceInitialAmount should be compared against the 
        // total amount NOT INCLUDING tax.
        if (cmDocument.isUseTaxIndicator()) {
            if (cmDocument.getGrandPreTaxTotal().compareTo(cmDocument.getCreditMemoAmount()) != 0 && !cmDocument.isUnmatchedOverride()) {
                KNSGlobalVariables.getMessageList().add(PurapKeyConstants.ERROR_CREDIT_MEMO_INVOICE_AMOUNT_NONMATCH);
                valid = false;
            }
        }
        // If NO UseTax, then the invoiceInitialAmount should be compared against the total amount
        // INCLUDING sales tax (since if the vendor invoices with sales tax), then we pay it.
        else {
            if (cmDocument.getGrandTotal().compareTo(cmDocument.getCreditMemoAmount()) != 0 && !cmDocument.isUnmatchedOverride()) {
                KNSGlobalVariables.getMessageList().add(PurapKeyConstants.ERROR_CREDIT_MEMO_INVOICE_AMOUNT_NONMATCH);
                valid = false;
            }
        }

        return valid;
    }

}
