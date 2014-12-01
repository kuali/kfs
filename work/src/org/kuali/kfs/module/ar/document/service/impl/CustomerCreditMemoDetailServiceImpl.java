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
package org.kuali.kfs.module.ar.document.service.impl;

import java.math.BigDecimal;

import org.kuali.kfs.module.ar.businessobject.CustomerCreditMemoDetail;
import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.module.ar.document.service.CustomerCreditMemoDetailService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;

public class CustomerCreditMemoDetailServiceImpl implements CustomerCreditMemoDetailService {

    public void recalculateCustomerCreditMemoDetail(CustomerCreditMemoDetail customerCreditMemoDetail, CustomerCreditMemoDocument customerCreditMemoDocument) {
        CustomerInvoiceDetailService service = SpringContext.getBean(CustomerInvoiceDetailService.class);
        
        String invDocumentNumber = customerCreditMemoDocument.getFinancialDocumentReferenceInvoiceNumber();
        Integer lineNumber = customerCreditMemoDetail.getReferenceInvoiceItemNumber();
        
        BigDecimal itemQuantity = customerCreditMemoDetail.getCreditMemoItemQuantity();
        KualiDecimal itemAmount = customerCreditMemoDetail.getCreditMemoItemTotalAmount();
        
        // if line amount was entered, it takes precedence, if not, use the item quantity to re-calc amount
        if (ObjectUtils.isNotNull(itemAmount)) {
            customerCreditMemoDetail.recalculateBasedOnEnteredItemAmount(customerCreditMemoDocument);
        }
        // if item amount was entered
        else {
            customerCreditMemoDetail.recalculateBasedOnEnteredItemQty(customerCreditMemoDocument);
        }
        
        customerCreditMemoDocument.recalculateTotalsBasedOnChangedItemAmount(customerCreditMemoDetail);                                                                                                                                                           
    }
}
