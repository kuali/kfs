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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.kuali.core.exceptions.InfrastructureException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DocumentService;
import org.kuali.module.ar.bo.AccountsReceivableDocumentHeader;
import org.kuali.module.ar.document.CustomerInvoiceDocument;
import org.kuali.module.ar.service.CustomerInvoiceDocumentService;
import org.kuali.module.financial.document.CashReceiptDocument;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.exception.WorkflowException;

@Transactional
public class CustomerInvoiceDocumentServiceImpl implements CustomerInvoiceDocumentService {
    
    private BusinessObjectService businessObjectService;
    private DocumentService documentService;

    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDocumentService#getCustomerInvoiceDocumentsByCustomerNumber(java.lang.String)
     */
    public List<CustomerInvoiceDocument> getCustomerInvoiceDocumentsByCustomerChartAndOrg(String customerNumber, String chartOfAccountsCode, String organizationCode) {
        
        List<CustomerInvoiceDocument> customerInvoiceDocuments = new ArrayList<CustomerInvoiceDocument>();
        
        Map fieldValues = new HashMap();
        fieldValues.put("customerNumber", customerNumber);
        fieldValues.put("processingChartOfAccountCode", chartOfAccountsCode);
        fieldValues.put("processingOrganizationCode", organizationCode);
        
        Collection<AccountsReceivableDocumentHeader> arDocHeaders = businessObjectService.findMatching(AccountsReceivableDocumentHeader.class, fieldValues);
        
        List<String> documentHeaderIds = new ArrayList<String>();
        for (AccountsReceivableDocumentHeader arDocHeader : arDocHeaders ) {
            documentHeaderIds.add(arDocHeader.getDocumentNumber());
        }
        
        try {
            customerInvoiceDocuments = documentService.getDocumentsByListOfDocumentHeaderIds(CustomerInvoiceDocument.class, documentHeaderIds);
        }
        catch (WorkflowException e) {
            throw new InfrastructureException("Unable to retrieve Customer Invoice Documents", e);
        }        
        
        return customerInvoiceDocuments;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public DocumentService getDocumentService() {
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

}
