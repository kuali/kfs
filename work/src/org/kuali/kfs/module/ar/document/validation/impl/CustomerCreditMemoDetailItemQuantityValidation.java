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
        boolean isValid;

        if (ObjectUtils.isNotNull(quantity) && ObjectUtils.isNull(amount)) {

            // customer credit memo quantity must be greater than zero
            isValid = (quantity.compareTo(BigDecimal.ZERO) == 1 ?true:false);
            if (!isValid) {
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_ITEM_QUANTITY, ArKeyConstants.ERROR_CUSTOMER_CREDIT_MEMO_DETAIL_ITEM_QUANTITY_LESS_THAN_OR_EQUAL_TO_ZERO);
                return false;
            }

            KualiDecimal invoiceOpenItemQty = customerCreditMemoDetail.getInvoiceOpenItemQuantity();
            KualiDecimal customerCreditMemoItemQty = new KualiDecimal(customerCreditMemoDetail.getCreditMemoItemQuantity());

            // customer credit memo quantity must not be greater than invoice open item quantity
            isValid = (customerCreditMemoItemQty.compareTo(invoiceOpenItemQty) < 1?true:false);
            if (!isValid) {
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
