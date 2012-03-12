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
