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

import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

import java.math.BigDecimal;

import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerCreditMemoDetail;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class CustomerCreditMemoDetailItemQuantityValidation extends GenericValidation {

    private CustomerCreditMemoDetail customerCreditMemoDetail;

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        BigDecimal quantity = customerCreditMemoDetail.getCreditMemoItemQuantity();
        KualiDecimal amount = customerCreditMemoDetail.getCreditMemoItemTotalAmount();
        boolean validValue;

        if (ObjectUtils.isNotNull(quantity) && ObjectUtils.isNull(amount)) {

            // customer credit memo quantity must be greater than zero
            validValue = (quantity.compareTo(BigDecimal.ZERO) == 1 ?true:false);
            if (!validValue) {
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_ITEM_QUANTITY, ArKeyConstants.ERROR_CUSTOMER_CREDIT_MEMO_DETAIL_ITEM_QUANTITY_LESS_THAN_OR_EQUAL_TO_ZERO);
                return false;
            }

            BigDecimal invoiceOpenItemQty = customerCreditMemoDetail.getInvoiceOpenItemQuantity();
            BigDecimal customerCreditMemoItemQty = customerCreditMemoDetail.getCreditMemoItemQuantity();

            // customer credit memo quantity must not be greater than invoice open item quantity
            validValue = (customerCreditMemoItemQty.compareTo(invoiceOpenItemQty) < 1?true:false);
            if (!validValue) {
                GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX + KFSConstants.CUSTOMER_CREDIT_MEMO_DETAIL_PROPERTY_NAME + "." + ArPropertyConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_ITEM_QUANTITY, ArKeyConstants.ERROR_CUSTOMER_CREDIT_MEMO_DETAIL_ITEM_QUANTITY_GREATER_THAN_INVOICE_ITEM_QUANTITY);
                return false;
            }
        }
        return true;
    }

    public CustomerCreditMemoDetail getCustomerCreditMemoDetail() {
        return customerCreditMemoDetail;
    }

    public void setCustomerCreditMemoDetail(CustomerCreditMemoDetail customerCreditMemoDetail) {
        this.customerCreditMemoDetail = customerCreditMemoDetail;
    }

}
