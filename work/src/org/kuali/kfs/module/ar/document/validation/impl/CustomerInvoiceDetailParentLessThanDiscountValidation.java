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
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class CustomerInvoiceDetailParentLessThanDiscountValidation extends GenericValidation {

    private CustomerInvoiceDocument customerInvoiceDocument;
    private CustomerInvoiceDetail parentCustomerInvoiceDetail;
    private CustomerInvoiceDetailService customerInvoiceDetailService;

    public boolean validate(AttributedDocumentEvent event) {
        boolean success = true;


        CustomerInvoiceDetail discountCustomerInvoiceDetail = parentCustomerInvoiceDetail.getDiscountCustomerInvoiceDetail();

        if (ObjectUtils.isNotNull(discountCustomerInvoiceDetail)) {

            // make a copy to not mess up the existing reference
            CustomerInvoiceDetail copyOfDiscountCustomerInvoiceDetail = new CustomerInvoiceDetail();
            copyOfDiscountCustomerInvoiceDetail.setInvoiceItemUnitPrice(discountCustomerInvoiceDetail.getInvoiceItemUnitPrice());
            copyOfDiscountCustomerInvoiceDetail.setInvoiceItemQuantity(discountCustomerInvoiceDetail.getInvoiceItemQuantity());

            // update amounts
            customerInvoiceDetailService.recalculateCustomerInvoiceDetail(customerInvoiceDocument, parentCustomerInvoiceDetail);
            customerInvoiceDetailService.recalculateCustomerInvoiceDetail(customerInvoiceDocument, copyOfDiscountCustomerInvoiceDetail);

            // return true if parent line amount is less THAN abs(discount line amount)
            if (parentCustomerInvoiceDetail.getAmount().abs().isLessThan(copyOfDiscountCustomerInvoiceDetail.getAmount().abs())) {
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_ITEM_UNIT_PRICE, ArKeyConstants.ERROR_CUSTOMER_INVOICE_DETAIL_DISCOUNT_AMOUNT_GREATER_THAN_PARENT_AMOUNT);
                success = false;
            }

        }

        return success;
    }

    public CustomerInvoiceDocument getCustomerInvoiceDocument() {
        return customerInvoiceDocument;
    }

    public void setCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument) {
        this.customerInvoiceDocument = customerInvoiceDocument;
    }

    public CustomerInvoiceDetail getParentCustomerInvoiceDetail() {
        return parentCustomerInvoiceDetail;
    }

    public void setParentCustomerInvoiceDetail(CustomerInvoiceDetail parentCustomerInvoiceDetail) {
        this.parentCustomerInvoiceDetail = parentCustomerInvoiceDetail;
    }

    public CustomerInvoiceDetailService getCustomerInvoiceDetailService() {
        return customerInvoiceDetailService;
    }

    public void setCustomerInvoiceDetailService(CustomerInvoiceDetailService customerInvoiceDetailService) {
        this.customerInvoiceDetailService = customerInvoiceDetailService;
    }

}
