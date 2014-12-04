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

import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.GlobalVariables;

public class CustomerInvoiceDetailAmountValidation extends GenericValidation {

    private CustomerInvoiceDocument customerInvoiceDocument;
    private CustomerInvoiceDetail customerInvoiceDetail;
    
    public boolean validate(AttributedDocumentEvent event) {

        KualiDecimal amount = customerInvoiceDetail.getAmount();
        
        if (KualiDecimal.ZERO.equals(amount)) {
            GlobalVariables.getMessageMap().putError(KFSConstants.AMOUNT_PROPERTY_NAME, ArKeyConstants.ERROR_CUSTOMER_INVOICE_DETAIL_TOTAL_AMOUNT_LESS_THAN_OR_EQUAL_TO_ZERO);
            return false;
        }
        else {
            // else if amount is greater than or less than zero

            if (customerInvoiceDocument.isInvoiceReversal()) {
                if (customerInvoiceDetail.isDiscountLine() && amount.isNegative()) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.AMOUNT_PROPERTY_NAME, ArKeyConstants.ERROR_CUSTOMER_INVOICE_DETAIL_TOTAL_AMOUNT_LESS_THAN_OR_EQUAL_TO_ZERO);
                    return false;
                }
                else if (!customerInvoiceDetail.isDiscountLine() && amount.isPositive()) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.AMOUNT_PROPERTY_NAME, ArKeyConstants.ERROR_CUSTOMER_INVOICE_DETAIL_TOTAL_AMOUNT_LESS_THAN_OR_EQUAL_TO_ZERO);
                    return false;
                }
            }
            else {
                if (customerInvoiceDetail.isDiscountLine() && amount.isPositive()) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.AMOUNT_PROPERTY_NAME, ArKeyConstants.ERROR_CUSTOMER_INVOICE_DETAIL_TOTAL_AMOUNT_LESS_THAN_OR_EQUAL_TO_ZERO);
                    return false;
                }
                else if (!customerInvoiceDetail.isDiscountLine() && amount.isNegative()) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.AMOUNT_PROPERTY_NAME, ArKeyConstants.ERROR_CUSTOMER_INVOICE_DETAIL_TOTAL_AMOUNT_LESS_THAN_OR_EQUAL_TO_ZERO);
                    return false;
                }
            }
        }

        return true;
    }    
    
    public CustomerInvoiceDocument getCustomerInvoiceDocument() {
        return customerInvoiceDocument;
    }

    public void setCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument) {
        this.customerInvoiceDocument = customerInvoiceDocument;
    }

    public CustomerInvoiceDetail getCustomerInvoiceDetail() {
        return customerInvoiceDetail;
    }

    public void setCustomerInvoiceDetail(CustomerInvoiceDetail customerInvoiceDetail) {
        this.customerInvoiceDetail = customerInvoiceDetail;
    }



}
