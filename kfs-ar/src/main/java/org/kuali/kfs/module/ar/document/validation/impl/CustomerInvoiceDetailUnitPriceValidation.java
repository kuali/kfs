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
