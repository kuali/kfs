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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ar.businessobject.CustomerCreditMemoDetail;
import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableTaxService;
import org.kuali.kfs.module.ar.document.service.CustomerCreditMemoDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

public class CustomerCreditMemoDocumentServiceImpl implements CustomerCreditMemoDocumentService {

    public void recalculateCustomerCreditMemoDocument(CustomerCreditMemoDocument customerCreditMemoDocument, boolean blanketApproveDocumentEventFlag) {
        KualiDecimal customerCreditMemoDetailItemAmount;
        BigDecimal itemQuantity;
        
        String invDocumentNumber = customerCreditMemoDocument.getFinancialDocumentReferenceInvoiceNumber();
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
            
            // if item amount was entered, it takes precedence, if not, use the item quantity to re-calc amount
            if (ObjectUtils.isNotNull(customerCreditMemoDetailItemAmount)) {
                customerCreditMemoDetail.recalculateBasedOnEnteredItemAmount(customerCreditMemoDocument);
            } // if item quantity was entered
            else {
                customerCreditMemoDetail.recalculateBasedOnEnteredItemQty(customerCreditMemoDocument);
                if (!blanketApproveDocumentEventFlag)
                    customerCreditMemoDetailItemAmount = customerCreditMemoDetail.getCreditMemoItemTotalAmount();
            }
            
            if (!blanketApproveDocumentEventFlag) {
                customerCreditMemoDetail.setDuplicateCreditMemoItemTotalAmount(customerCreditMemoDetailItemAmount);
                boolean isCustomerInvoiceDetailTaxable = SpringContext.getBean(AccountsReceivableTaxService.class).isCustomerInvoiceDetailTaxable(customerCreditMemoDocument.getInvoice(), customerCreditMemoDetail.getCustomerInvoiceDetail());
                customerCreditMemoDocument.recalculateTotals(customerCreditMemoDetailItemAmount,isCustomerInvoiceDetailTaxable);
            }
        }
    }

    public Collection<CustomerCreditMemoDocument> getCustomerCreditMemoDocumentByInvoiceDocument(String invoiceNumber) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put("financialDocumentReferenceInvoiceNumber", invoiceNumber);
        BusinessObjectService service = SpringContext.getBean(BusinessObjectService.class);
        
        Collection<CustomerCreditMemoDocument> creditMemos = 
            service.findMatching(CustomerCreditMemoDocument.class, fieldValues);
        
        return creditMemos;
    }

    public boolean isThereNoDataToSubmit(CustomerCreditMemoDocument customerCreditMemoDocument) {
        boolean success = true;
        KualiDecimal customerCreditMemoDetailItemAmount;
        BigDecimal itemQuantity;
        List<CustomerCreditMemoDetail> customerCreditMemoDetails = customerCreditMemoDocument.getCreditMemoDetails();

        for (CustomerCreditMemoDetail customerCreditMemoDetail:customerCreditMemoDetails) {
            // no data entered for the current credit memo detail -> no processing needed
            itemQuantity = customerCreditMemoDetail.getCreditMemoItemQuantity();
            customerCreditMemoDetailItemAmount = customerCreditMemoDetail.getCreditMemoItemTotalAmount();
            if (ObjectUtils.isNotNull(itemQuantity) || ObjectUtils.isNotNull(customerCreditMemoDetailItemAmount)) {
                success = false;
                break;
            }
        }
        return success;
    }
}
