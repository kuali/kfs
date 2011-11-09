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
package org.kuali.kfs.module.ar.document.validation.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.document.WorkflowDocumentService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.exception.UnknownDocumentIdException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;

public class CustomerCreditMemoNoOtherCRMInRouteForTheInvoiceValidation extends GenericValidation {

    private CustomerCreditMemoDocument customerCreditMemoDocument;
    private BusinessObjectService businessObjectService;
    private WorkflowDocumentService workflowDocumentService;
    
    public boolean validate(AttributedDocumentEvent event) {
    
        String invoiceDocumentNumber = customerCreditMemoDocument.getFinancialDocumentReferenceInvoiceNumber();
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
        
        Person user = GlobalVariables.getUserSession().getPerson();
        
        for(CustomerCreditMemoDocument customerCreditMemoDocument : customerCreditMemoDocuments) {
            try {
                workflowDocument = SpringContext.getBean(WorkflowDocumentService.class).createWorkflowDocument(Long.valueOf(customerCreditMemoDocument.getDocumentNumber()), user);
            }
            catch (WorkflowException e) {
                throw new UnknownDocumentIdException("no document found for documentHeaderId '" + customerCreditMemoDocument.getDocumentNumber() + "'", e);
            }
            
            if (!(workflowDocument.isApproved() || workflowDocument.isProcessed() || workflowDocument.isCanceled() || workflowDocument.isDisapproved())) {
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_DOCUMENT_REF_INVOICE_NUMBER, ArKeyConstants.ERROR_CUSTOMER_CREDIT_MEMO_DOCUMENT_ONE_CRM_IN_ROUTE_PER_INVOICE);
                return false;
            }
        }
        return true;
    
    }
    
    public CustomerCreditMemoDocument getCustomerCreditMemoDocument() {
        return customerCreditMemoDocument;
    }

    public void setCustomerCreditMemoDocument(CustomerCreditMemoDocument customerCreditMemoDocument) {
        this.customerCreditMemoDocument = customerCreditMemoDocument;
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
