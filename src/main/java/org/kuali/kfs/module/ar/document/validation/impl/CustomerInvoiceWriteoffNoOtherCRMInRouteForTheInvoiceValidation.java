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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceWriteoffDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.WorkflowDocumentFactory;
import org.kuali.rice.kew.api.document.WorkflowDocumentService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;

public class CustomerInvoiceWriteoffNoOtherCRMInRouteForTheInvoiceValidation extends GenericValidation {

    private CustomerInvoiceWriteoffDocument customerInvoiceWriteoffDocument;
    private BusinessObjectService businessObjectService;
    private WorkflowDocumentService workflowDocumentService;
    
    public boolean validate(AttributedDocumentEvent event) {
    
        String invoiceDocumentNumber = customerInvoiceWriteoffDocument.getFinancialDocumentReferenceInvoiceNumber();
        WorkflowDocument workflowDocument;
        boolean success = true;
        
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put("financialDocumentReferenceInvoiceNumber", invoiceDocumentNumber);
        
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        Collection<CustomerCreditMemoDocument> customerCreditMemoDocuments = 
            businessObjectService.findMatching(CustomerCreditMemoDocument.class, fieldValues);
        
        // no CRMs associated with the invoice are found
        if (customerCreditMemoDocuments.isEmpty())
            return true;
        
        String userId = GlobalVariables.getUserSession().getPrincipalId();        
        for(CustomerCreditMemoDocument customerCreditMemoDocument : customerCreditMemoDocuments) {
            workflowDocument = WorkflowDocumentFactory.loadDocument(userId, customerCreditMemoDocument.getDocumentNumber());
            if (!(workflowDocument.isApproved() || workflowDocument.isProcessed() || workflowDocument.isCanceled() || workflowDocument.isDisapproved())) {
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_DOCUMENT_REF_INVOICE_NUMBER, ArKeyConstants.ERROR_CUSTOMER_CREDIT_MEMO_DOCUMENT_ONE_CRM_IN_ROUTE_PER_INVOICE);
                return false;
            }
        }
        return true;
    
    }
    
    public CustomerInvoiceWriteoffDocument getCustomerInvoiceWriteoffDocument() {
        return customerInvoiceWriteoffDocument;
    }

    public void setCustomerInvoiceWriteoffDocument(CustomerInvoiceWriteoffDocument customerInvoiceWriteoffDocument) {
        this.customerInvoiceWriteoffDocument = customerInvoiceWriteoffDocument;
    }

    public WorkflowDocumentService getWorkflowDocumentService() {
        return workflowDocumentService;
    }

    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }    

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }    

}
