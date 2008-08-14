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
package org.kuali.kfs.module.ar.document.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.kuali.kfs.module.ar.businessobject.CustomerCreditMemoDetail;
import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.module.ar.document.service.CustomerCreditMemoDocumentService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

public class CustomerCreditMemoDocumentServiceImpl implements CustomerCreditMemoDocumentService {

    public void recalculateCustomerCreditMemoDocument(CustomerCreditMemoDocument customerCreditMemoDocument, boolean blanketApproveDocumentEventFlag) {
        KualiDecimal customerCreditMemoDetailItemAmount;
        BigDecimal itemQuantity;
        
        String invDocumentNumber = customerCreditMemoDocument.getFinancialDocumentReferenceInvoiceNumber();
        KualiDecimal taxRate = customerCreditMemoDocument.getTaxRate();
        List<CustomerCreditMemoDetail> customerCreditMemoDetails = customerCreditMemoDocument.getCreditMemoDetails();
        
        if (!blanketApproveDocumentEventFlag)
            customerCreditMemoDocument.resetTotals();
        
        for (CustomerCreditMemoDetail customerCreditMemoDetail:customerCreditMemoDetails) {
            // no data entered for the current credit memo detail -> no processing needed
            itemQuantity = customerCreditMemoDetail.getCreditMemoItemQuantity();
            customerCreditMemoDetailItemAmount = customerCreditMemoDetail.getCreditMemoItemTotalAmount();
            if (ObjectUtils.isNull(itemQuantity) && ObjectUtils.isNull(customerCreditMemoDetailItemAmount)) {
                if (!blanketApproveDocumentEventFlag)
                    customerCreditMemoDetail.setDuplicateCreditMemoItemTotalAmount(null);
                continue;
            }
            
            // if item quantity was entered
            if (ObjectUtils.isNotNull(itemQuantity)) {
                customerCreditMemoDetail.recalculateBasedOnEnteredItemQty(taxRate);
                if (!blanketApproveDocumentEventFlag)
                    customerCreditMemoDetailItemAmount = customerCreditMemoDetail.getCreditMemoItemTotalAmount();
            } // if item amount was entered
            else {
                customerCreditMemoDetail.recalculateBasedOnEnteredItemAmount(taxRate);
            }
            if (!blanketApproveDocumentEventFlag) {
                customerCreditMemoDetail.setDuplicateCreditMemoItemTotalAmount(customerCreditMemoDetailItemAmount);
                customerCreditMemoDocument.recalculateTotals(customerCreditMemoDetailItemAmount);
            }
        }
    }
}
