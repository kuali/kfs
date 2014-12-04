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
