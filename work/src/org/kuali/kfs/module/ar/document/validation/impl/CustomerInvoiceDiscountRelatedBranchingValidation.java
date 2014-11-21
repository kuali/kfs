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
