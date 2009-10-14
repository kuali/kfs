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

import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.sys.document.validation.BranchingValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

public class CustomerInvoiceDiscountRelatedBranchingValidation extends BranchingValidation {
    
    public static final String IS_DISCOUNT_VALIDATION = "isDiscountValidation";
    public static final String IS_PARENT_VALIDATION = "isParentValidation";
    
    protected CustomerInvoiceDetail customerInvoiceDetail;

    @Override
    protected String determineBranch(AttributedDocumentEvent event) {
        if (customerInvoiceDetail.isDiscountLine()) {
            return IS_DISCOUNT_VALIDATION;
        } else if (customerInvoiceDetail.isDiscountLineParent()) {
           return IS_PARENT_VALIDATION;
        }
        return null;
    }
    
    public CustomerInvoiceDetail getCustomerInvoiceDetail() {
        return customerInvoiceDetail;
    }

    public void setCustomerInvoiceDetail(CustomerInvoiceDetail customerInvoiceDetail) {
        this.customerInvoiceDetail = customerInvoiceDetail;
    }

}
