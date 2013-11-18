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
