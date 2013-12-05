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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.ar.businessobject.AppliedPayment;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.InvoicePaidAppliedService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class InvoicePaidAppliedServiceImpl implements InvoicePaidAppliedService<AppliedPayment> {

    private BusinessObjectService businessObjectService;
    private UniversityDateService universityDateService;

    /**
     * @see org.kuali.kfs.module.ar.document.service.InvoicePaidAppliedService#clearDocumentPaidAppliedsFromDatabase(java.lang.String)
     */
    public void clearDocumentPaidAppliedsFromDatabase(String documentNumber) {
        Map<String,String> fields = new HashMap<String,String>();
        fields.put("documentNumber", documentNumber);
        businessObjectService.deleteMatching(InvoicePaidApplied.class, fields);
    }

    public Integer getNumberOfInvoicePaidAppliedsForInvoiceDetail(String financialDocumentReferenceInvoiceNumber, Integer invoiceItemNumber){
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put("financialDocumentReferenceInvoiceNumber", financialDocumentReferenceInvoiceNumber);
        criteria.put("invoiceItemNumber", invoiceItemNumber);
        
        return businessObjectService.countMatching(InvoicePaidApplied.class, criteria);
    }
    
    public Collection<InvoicePaidApplied> getInvoicePaidAppliedsFromSpecificDocument(String documentNumber, String referenceCustomerInvoiceDocumentNumber) {
        Map criteria = new HashMap();
        criteria.put("financialDocumentReferenceInvoiceNumber", referenceCustomerInvoiceDocumentNumber);
        criteria.put("documentNumber", documentNumber);
        return businessObjectService.findMatching(InvoicePaidApplied.class, criteria);
    }    
            
        

    /**
     * @see org.kuali.kfs.module.ar.document.service.InvoicePaidAppliedService#doesInvoiceHaveAppliedAmounts(org.kuali.kfs.module.ar.document.CustomerInvoiceDocument)
     */
    public boolean doesInvoiceHaveAppliedAmounts(CustomerInvoiceDocument document) {

        HashMap<String, String> criteria = new HashMap<String, String>();
        criteria.put("financialDocumentReferenceInvoiceNumber", document.getDocumentNumber());
        
        Collection<InvoicePaidApplied> results = businessObjectService.findMatching(InvoicePaidApplied.class, criteria);
        for( InvoicePaidApplied invoicePaidApplied : results ){
            //don't include discount (the doc num and the ref num are the same document number)
            if( !invoicePaidApplied.getDocumentNumber().equals(document.getDocumentNumber())){
                return true;
            }
        }
        return false;
    }    

    /**
     * @see org.kuali.kfs.module.ar.document.service.InvoicePaidAppliedService#getInvoicePaidAppliedsForInvoice(java.lang.String)
     */
    public Collection<InvoicePaidApplied> getInvoicePaidAppliedsForInvoice(String documentNumber) {
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("financialDocumentReferenceInvoiceNumber", documentNumber);
        return businessObjectService.findMatching(InvoicePaidApplied.class, criteria);
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.InvoicePaidAppliedService#getInvoicePaidAppliedsForInvoice(org.kuali.kfs.module.ar.document.CustomerInvoiceDocument)
     */
    public Collection<InvoicePaidApplied> getInvoicePaidAppliedsForInvoice(CustomerInvoiceDocument invoice) {
        return getInvoicePaidAppliedsForInvoice(invoice.getDocumentNumber());
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
