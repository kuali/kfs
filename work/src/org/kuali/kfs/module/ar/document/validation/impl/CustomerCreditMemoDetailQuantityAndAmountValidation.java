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

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerCreditMemoDetail;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class CustomerCreditMemoDetailQuantityAndAmountValidation extends GenericValidation {

    protected BigDecimal getAllowedQtyDeviation() {
        return new BigDecimal("0.10");
    }

    private CustomerCreditMemoDetail customerCreditMemoDetail;

    @Override
    public boolean validate(AttributedDocumentEvent event) {

        KualiDecimal creditAmount = customerCreditMemoDetail.getCreditMemoItemTotalAmount();
        BigDecimal quantity = customerCreditMemoDetail.getCreditMemoItemQuantity();
        BigDecimal unitPrice = customerCreditMemoDetail.getCustomerInvoiceDetail().getInvoiceItemUnitPrice();
        boolean isValid;

        if (ObjectUtils.isNotNull(quantity) && ObjectUtils.isNotNull(creditAmount)) {

            //  determine the expected exact total credit memo quantity, based on actual credit amount entered
            BigDecimal creditQuantity = customerCreditMemoDetail.getCreditMemoItemQuantity();
            BigDecimal expectedCreditQuantity = creditAmount.bigDecimalValue().divide(unitPrice, ArConstants.ITEM_QUANTITY_SCALE, BigDecimal.ROUND_HALF_UP);

            // return false if the expected quantity is 0 while the actual quantity is not
            if (expectedCreditQuantity.compareTo(BigDecimal.ZERO) == 0 && creditQuantity.compareTo(BigDecimal.ZERO) != 0) {
                return false;
            }

            //  determine the deviation percentage that the actual creditQuantity has from expectedCreditQuantity
            KualiDecimal deviationPercentage = expectedCreditQuantity.subtract(creditQuantity).abs().divide(expectedCreditQuantity);
        
            // only allow a certain deviation of creditQuantity from the expectedCreditQuantity 
            isValid = (deviationPercentage.isLessEqual(getAllowedQtyDeviation()));
            if (!isValid){
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_ITEM_QUANTITY, ArKeyConstants.ERROR_CUSTOMER_CREDIT_MEMO_DETAIL_INVALID_DATA_INPUT);
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_ITEM_TOTAL_AMOUNT, ArKeyConstants.ERROR_CUSTOMER_CREDIT_MEMO_DETAIL_INVALID_DATA_INPUT);
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
