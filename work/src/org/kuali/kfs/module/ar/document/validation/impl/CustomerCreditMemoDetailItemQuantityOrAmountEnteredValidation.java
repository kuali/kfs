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

import org.kuali.kfs.module.ar.businessobject.CustomerCreditMemoDetail;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;

public class CustomerCreditMemoDetailItemQuantityOrAmountEnteredValidation extends GenericValidation {

    private CustomerCreditMemoDetail customerCreditMemoDetail;
    
    public boolean validate(AttributedDocumentEvent event) {
        BigDecimal quantity = customerCreditMemoDetail.getCreditMemoItemQuantity();
        KualiDecimal amount = customerCreditMemoDetail.getCreditMemoItemTotalAmount();
        boolean isValid;

        // don't generate error message but fail the business rules.
        if (ObjectUtils.isNull(quantity) && ObjectUtils.isNull(amount)) {
            return false;
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
