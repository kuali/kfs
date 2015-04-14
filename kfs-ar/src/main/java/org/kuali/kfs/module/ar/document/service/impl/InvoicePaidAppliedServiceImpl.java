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
