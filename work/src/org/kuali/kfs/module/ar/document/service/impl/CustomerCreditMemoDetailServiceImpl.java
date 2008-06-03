/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.ar.service.impl;

import java.math.BigDecimal;

import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.ar.bo.CustomerCreditMemoDetail;
import org.kuali.module.ar.bo.CustomerInvoiceDetail;
import org.kuali.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.module.ar.service.CustomerCreditMemoDetailService;
import org.kuali.module.ar.service.CustomerInvoiceDetailService;

public class CustomerCreditMemoDetailServiceImpl implements CustomerCreditMemoDetailService {

    public void recalculateCustomerCreditMemoDetail(CustomerCreditMemoDetail customerCreditMemoDetail, CustomerCreditMemoDocument customerCreditMemoDocument) {
        CustomerInvoiceDetailService service = SpringContext.getBean(CustomerInvoiceDetailService.class);
        
        String invDocumentNumber = customerCreditMemoDocument.getFinancialDocumentReferenceInvoiceNumber();
        Integer lineNumber = customerCreditMemoDetail.getReferenceInvoiceItemNumber();
        CustomerInvoiceDetail customerInvoiceDetail = service.getCustomerInvoiceDetail(invDocumentNumber,lineNumber);
        
        KualiDecimal invItemUnitPrice = customerInvoiceDetail.getInvoiceItemUnitPrice();
        BigDecimal itemQuantity = customerCreditMemoDetail.getCreditMemoItemQuantity();
        KualiDecimal taxRate = customerCreditMemoDocument.getTaxRate();

        // if item quantity was entered
        if (ObjectUtils.isNotNull(itemQuantity))
            customerCreditMemoDetail.recalculateBasedOnEnteredItemQty(taxRate,invItemUnitPrice);
        // if item amount was entered
        else
            customerCreditMemoDetail.recalculateBasedOnEnteredItemAmount(taxRate,invItemUnitPrice);
        
        customerCreditMemoDocument.recalculateTotalsBasedOnChangedItemAmount(customerCreditMemoDetail);                                                                                                                                                           
    }
}