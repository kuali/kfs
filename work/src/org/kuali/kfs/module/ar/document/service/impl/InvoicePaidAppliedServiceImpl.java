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

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.module.ar.bo.CustomerInvoiceDetail;
import org.kuali.module.ar.bo.InvoicePaidApplied;
import org.kuali.module.ar.service.InvoicePaidAppliedService;
import org.kuali.module.financial.service.UniversityDateService;

public class InvoicePaidAppliedServiceImpl implements InvoicePaidAppliedService {

    private BusinessObjectService businessObjectService;
    private UniversityDateService universityDateService;
    
    /**
     * @see org.kuali.module.ar.service.InvoicePaidAppliedService#saveInvoicePaidAppliedForDiscounts(java.util.List)
     */
    public void saveInvoicePaidAppliedForDiscounts(List<CustomerInvoiceDetail> customerInvoiceDetails) {
        
        List<InvoicePaidApplied> invoicePaidAppliedAmounts = new ArrayList<InvoicePaidApplied>();
        
        InvoicePaidApplied invoicePaidApplied;
        CustomerInvoiceDetail discount;
        for( CustomerInvoiceDetail customerInvoiceDetail : customerInvoiceDetails ){
            if ( customerInvoiceDetail.isDiscountLineParent() ){
                discount = getDiscountLineBasedOnSequenceNumberFromParent( customerInvoiceDetails, customerInvoiceDetail.getInvoiceItemDiscountLineNumber() );
                
                invoicePaidApplied = new InvoicePaidApplied();
                invoicePaidApplied.setDocumentNumber(customerInvoiceDetail.getDocumentNumber());
                invoicePaidApplied.setPaidAppliedItemNumber(invoicePaidAppliedAmounts.size());
                invoicePaidApplied.setFinancialDocumentReferenceInvoiceNumber(customerInvoiceDetail.getDocumentNumber());
                invoicePaidApplied.setInvoiceItemNumber(customerInvoiceDetail.getSequenceNumber());
                invoicePaidApplied.setUniversityFiscalYear(universityDateService.getCurrentFiscalYear());
                invoicePaidApplied.setUniversityFiscalPeriodCode(universityDateService.getCurrentUniversityDate().getUniversityFiscalAccountingPeriod());
                invoicePaidApplied.setInvoiceItemAppliedAmount(discount.getAmount().negated());
                invoicePaidAppliedAmounts.add(invoicePaidApplied);
            }
            
        }
        
        businessObjectService.save(invoicePaidAppliedAmounts);

    }
    
    /**
     * This method returns a customer discount line based on the discount line sequence number reference from the parent
     * @param customerInvoiceDetails
     * @param discountSequenceNumber
     * @return
     */
    protected CustomerInvoiceDetail getDiscountLineBasedOnSequenceNumberFromParent(List<CustomerInvoiceDetail> customerInvoiceDetails, Integer discountSequenceNumber){
        
        for( CustomerInvoiceDetail customerInvoiceDetail : customerInvoiceDetails  ){
            if( discountSequenceNumber.equals( customerInvoiceDetail.getSequenceNumber() ) ){
                return customerInvoiceDetail;
            }
        }
        return null;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

}
