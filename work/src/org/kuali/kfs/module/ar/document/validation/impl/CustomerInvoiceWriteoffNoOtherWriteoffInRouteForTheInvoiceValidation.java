/*
 * Copyright 2008-2009 The Kuali Foundation
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

import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.document.CustomerInvoiceWriteoffDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.exception.UnknownDocumentIdException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.document.WorkflowDocumentService;

public class CustomerInvoiceWriteoffNoOtherWriteoffInRouteForTheInvoiceValidation extends GenericValidation {

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
        Collection<CustomerInvoiceWriteoffDocument> customerInvoiceWriteoffDocuments = 
            businessObjectService.findMatching(CustomerInvoiceWriteoffDocument.class, fieldValues);
        
        // no Writeoffs associated with the invoice are found
        if (customerInvoiceWriteoffDocuments.isEmpty())
            return true;
        
        Person user = GlobalVariables.getUserSession().getPerson();
        
        for(CustomerInvoiceWriteoffDocument customerInvoiceWriteoffDocument : customerInvoiceWriteoffDocuments) {
            try {
                workflowDocument = SpringContext.getBean(WorkflowDocumentService.class).createWorkflowDocument(Long.valueOf(customerInvoiceWriteoffDocument.getDocumentNumber()), user);
            }
            catch (WorkflowException e) {
                throw new UnknownDocumentIdException("no document found for documentHeaderId '" + customerInvoiceWriteoffDocument.getDocumentNumber() + "'", e);
            }
            
            if (!(workflowDocument.isApproved() || workflowDocument.isProcessed() || workflowDocument.isCanceled() || workflowDocument.isDisapproved())) {
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_DOCUMENT_REF_INVOICE_NUMBER, ArKeyConstants.ERROR_CUSTOMER_INVOICE_WRITEOFF_ONE_WRITEOFF_IN_ROUTE_PER_INVOICE);
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
