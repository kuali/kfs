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

import java.math.BigDecimal;

import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class CustomerInvoiceDetailUnitPriceValidation extends GenericValidation {

    private CustomerInvoiceDetail customerInvoiceDetail;
    private CustomerInvoiceDocument customerInvoiceDocument;
    
    public boolean validate(AttributedDocumentEvent event) {
        BigDecimal unitPrice = customerInvoiceDetail.getInvoiceItemUnitPrice();

        // if amount is = 0
        if (ObjectUtils.isNull(unitPrice) || BigDecimal.ZERO.equals(unitPrice)) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_ITEM_UNIT_PRICE, ArKeyConstants.ERROR_CUSTOMER_INVOICE_DETAIL_UNIT_PRICE_LESS_THAN_OR_EQUAL_TO_ZERO);
            return false;
        }
        else {
            // else if unit price is greater than or less than zero

            if (customerInvoiceDocument.isInvoiceReversal()) {
                // if invoice is reversal, discount should be positive, non-discounts should be negative
                if (customerInvoiceDetail.isDiscountLine() && unitPrice.compareTo(BigDecimal.ZERO) == -1) {
                    GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_ITEM_UNIT_PRICE, ArKeyConstants.ERROR_CUSTOMER_INVOICE_DETAIL_UNIT_PRICE_LESS_THAN_OR_EQUAL_TO_ZERO);
                    return false;
                }
                else if (!customerInvoiceDetail.isDiscountLine() && unitPrice.compareTo(BigDecimal.ZERO) == 1) {
                    GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_ITEM_UNIT_PRICE, ArKeyConstants.ERROR_CUSTOMER_INVOICE_DETAIL_UNIT_PRICE_LESS_THAN_OR_EQUAL_TO_ZERO);
                    return false;
                }
            }
            else {
                //No need to check for positive because if the line is a discount and the amount is negative, we will negate the line.
                if (!customerInvoiceDetail.isDiscountLine() && unitPrice.compareTo(BigDecimal.ZERO) == -1) {
                    GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_ITEM_UNIT_PRICE, ArKeyConstants.ERROR_CUSTOMER_INVOICE_DETAIL_UNIT_PRICE_LESS_THAN_OR_EQUAL_TO_ZERO);
                    return false;
                }
            }
        }
        
        return true;
    }
    
    public CustomerInvoiceDetail getCustomerInvoiceDetail() {
        return customerInvoiceDetail;
    }

    public void setCustomerInvoiceDetail(CustomerInvoiceDetail customerInvoiceDetail) {
        this.customerInvoiceDetail = customerInvoiceDetail;
    }

    public CustomerInvoiceDocument getCustomerInvoiceDocument() {
        return customerInvoiceDocument;
    }

    public void setCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument) {
        this.customerInvoiceDocument = customerInvoiceDocument;
    }        
}
