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
        boolean validValue;

        if (ObjectUtils.isNotNull(quantity) && ObjectUtils.isNotNull(creditAmount)) {

            //  determine the expected exact total credit memo quantity, based on actual credit amount entered
            BigDecimal creditQuantity = customerCreditMemoDetail.getCreditMemoItemQuantity();
            BigDecimal expectedCreditQuantity = creditAmount.bigDecimalValue().divide(unitPrice, ArConstants.ITEM_QUANTITY_SCALE, BigDecimal.ROUND_HALF_UP);

            // return false if the expected quantity is 0 while the actual quantity is not
            if (expectedCreditQuantity.compareTo(BigDecimal.ZERO) == 0 && creditQuantity.compareTo(BigDecimal.ZERO) != 0) {
                return false;
            }

            //  determine the deviation percentage that the actual creditQuantity has from expectedCreditQuantity
            BigDecimal deviationPercentage = creditQuantity.subtract(expectedCreditQuantity).divide(expectedCreditQuantity, ArConstants.ITEM_QUANTITY_SCALE, BigDecimal.ROUND_HALF_UP).abs();

            // only allow a certain deviation of creditQuantity from the expectedCreditQuantity
            validValue = deviationPercentage.compareTo(getAllowedQtyDeviation()) < 1;
            if (!validValue){
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
