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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ar.businessobject.CustomerCreditMemoDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableTaxService;
import org.kuali.kfs.module.ar.document.service.CustomerCreditMemoDocumentService;
import org.kuali.kfs.module.ar.document.service.InvoicePaidAppliedService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;

public class CustomerCreditMemoDocumentServiceImpl implements CustomerCreditMemoDocumentService {

    private DocumentService documentService;
    private InvoicePaidAppliedService<CustomerInvoiceDetail> paidAppliedService;
    private UniversityDateService universityDateService;
    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;
    private AccountsReceivableTaxService accountsReceivableTaxService;
    
    public void completeCustomerCreditMemo(CustomerCreditMemoDocument creditMemo) {
        
        //  retrieve the document and make sure its not already closed, crash if so 
        String invoiceNumber = creditMemo.getFinancialDocumentReferenceInvoiceNumber();
        CustomerInvoiceDocument invoice;
        try {
             invoice = (CustomerInvoiceDocument) documentService.getByDocumentHeaderId(invoiceNumber);
        }
        catch (WorkflowException e) {
            throw new RuntimeException("A WorkflowException was generated when trying to load Customer Invoice #" + invoiceNumber + ".", e);
        }
        if (!invoice.isOpenInvoiceIndicator()) {
            throw new UnsupportedOperationException("The CreditMemo Document #" + creditMemo.getDocumentNumber() + " attempted to credit " + 
                    "an Invoice [#" + invoiceNumber + "] that was already closed.  This is not supported.");
        }
        
        // this needs a little explanation.  we have to calculate manually 
        // whether we've written off the whole thing, because the regular 
        // code uses the invoice paid applieds to discount, but since those 
        // are added but not committed in this transaction, they're also not 
        // visible in this transaction, so we do it manually.
        KualiDecimal openAmount = invoice.getOpenAmount();

        Integer paidAppliedItemNumber = 0;
        
        //  retrieve the customer invoice details, and generate paid applieds for each 
        List<CustomerCreditMemoDetail> details = creditMemo.getCreditMemoDetails();
        for (CustomerCreditMemoDetail detail : details) {
            CustomerInvoiceDetail invoiceDetail = detail.getCustomerInvoiceDetail();
            
            //   if credit amount is zero, do nothing
            if (detail.getCreditMemoLineTotalAmount().isZero()) {
                continue;
            }
            
            //  if credit amount is greater than the open amount, crash and complain
            if (detail.getCreditMemoLineTotalAmount().abs().isGreaterThan(invoiceDetail.getAmountOpen())) {
                throw new UnsupportedOperationException("The credit detail for CreditMemo Document #" + creditMemo.getDocumentNumber() + " attempted " +
                        "to credit more than the Open Amount on the Invoice Detail.  This is not supported.");
            }
            
            //  retrieve the number of current paid applieds, so we dont have item number overlap
            if (paidAppliedItemNumber == 0) {
                paidAppliedItemNumber = paidAppliedService.getNumberOfInvoicePaidAppliedsForInvoiceDetail(invoiceNumber, 
                        invoiceDetail.getInvoiceItemNumber());
            }
            
            
            //  create and save the paidApplied
            InvoicePaidApplied invoicePaidApplied = new InvoicePaidApplied();
            invoicePaidApplied.setDocumentNumber(creditMemo.getDocumentNumber());
            invoicePaidApplied.setPaidAppliedItemNumber(paidAppliedItemNumber++);
            invoicePaidApplied.setFinancialDocumentReferenceInvoiceNumber(invoiceNumber);
            invoicePaidApplied.setInvoiceItemNumber(invoiceDetail.getInvoiceItemNumber());
            invoicePaidApplied.setUniversityFiscalYear(universityDateService.getCurrentFiscalYear());
            invoicePaidApplied.setUniversityFiscalPeriodCode(universityDateService.getCurrentUniversityDate().getUniversityFiscalAccountingPeriod());
            invoicePaidApplied.setInvoiceItemAppliedAmount(detail.getCreditMemoLineTotalAmount().abs());
            openAmount = openAmount.subtract(detail.getCreditMemoLineTotalAmount().abs());
            businessObjectService.save(invoicePaidApplied);
       }
        
       //   if its open, but now with a zero openamount, then close it
       if (invoice.isOpenInvoiceIndicator() && KualiDecimal.ZERO.equals(openAmount)) {
           invoice.setOpenInvoiceIndicator(false);
           invoice.setClosedDate(dateTimeService.getCurrentSqlDate());
           documentService.updateDocument(invoice);
       }
    }
    
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
                boolean isCustomerInvoiceDetailTaxable = accountsReceivableTaxService.isCustomerInvoiceDetailTaxable(customerCreditMemoDocument.getInvoice(), customerCreditMemoDetail.getCustomerInvoiceDetail());
                customerCreditMemoDocument.recalculateTotals(customerCreditMemoDetailItemAmount,isCustomerInvoiceDetailTaxable);
            }
        }
        
        //  force the docHeader docTotal
        customerCreditMemoDocument.getFinancialSystemDocumentHeader().setFinancialDocumentTotalAmount(customerCreditMemoDocument.getCrmTotalAmount());
    }

    public Collection<CustomerCreditMemoDocument> getCustomerCreditMemoDocumentByInvoiceDocument(String invoiceNumber) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put("financialDocumentReferenceInvoiceNumber", invoiceNumber);
        
        Collection<CustomerCreditMemoDocument> creditMemos = 
            businessObjectService.findMatching(CustomerCreditMemoDocument.class, fieldValues);
        
        return creditMemos;
    }
    
    public boolean isThereNoDataToSubmit(CustomerCreditMemoDocument customerCreditMemoDocument) {
        boolean isSuccess = true;
        KualiDecimal customerCreditMemoDetailItemAmount;
        BigDecimal itemQuantity;
        List<CustomerCreditMemoDetail> customerCreditMemoDetails = customerCreditMemoDocument.getCreditMemoDetails();

        for (CustomerCreditMemoDetail customerCreditMemoDetail:customerCreditMemoDetails) {
            // no data entered for the current credit memo detail -> no processing needed
            itemQuantity = customerCreditMemoDetail.getCreditMemoItemQuantity();
            customerCreditMemoDetailItemAmount = customerCreditMemoDetail.getCreditMemoItemTotalAmount();
            if (ObjectUtils.isNotNull(itemQuantity) || ObjectUtils.isNotNull(customerCreditMemoDetailItemAmount)) {
                isSuccess = false;
                break;
            }
        }
        return isSuccess;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setPaidAppliedService(InvoicePaidAppliedService<CustomerInvoiceDetail> paidAppliedService) {
        this.paidAppliedService = paidAppliedService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
    
    public AccountsReceivableTaxService getAccountsReceivableTaxService() {
        return accountsReceivableTaxService;
    }

    public void setAccountsReceivableTaxService(AccountsReceivableTaxService accountsReceivableTaxService) {
        this.accountsReceivableTaxService = accountsReceivableTaxService;
    }
}
