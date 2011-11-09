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
