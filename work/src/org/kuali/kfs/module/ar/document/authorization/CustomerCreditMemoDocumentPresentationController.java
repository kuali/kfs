/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.authorization;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArAuthorizationConstants;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.ObjectUtils;

public class CustomerCreditMemoDocumentPresentationController extends FinancialSystemTransactionalDocumentPresentationControllerBase {

    @Override
    public Set<String> getEditModes(Document document) {
        
        Set<String> editModes = super.getEditModes(document);
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument) document;
        
        if (StringUtils.equals(customerCreditMemoDocument.getStatusCode(), ArConstants.CustomerCreditMemoStatuses.INITIATE)) {
            editModes.add(ArAuthorizationConstants.CustomerCreditMemoEditMode.DISPLAY_INIT_TAB);
        }
        
        boolean salesTaxInd = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean("KFS-AR", "Document", ArConstants.ENABLE_SALES_TAX_IND);
        if (salesTaxInd) {
            editModes.add(ArAuthorizationConstants.SALES_TAX_ENABLED);
        }
        
        String receivableOffsetOption = SpringContext.getBean(ParameterService.class).getParameterValueAsString(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        if (ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU.equals(receivableOffsetOption)){
            editModes.add(ArAuthorizationConstants.CustomerInvoiceDocumentEditMode.SHOW_RECEIVABLE_FAU);
        }
        
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (ObjectUtils.isNotNull(workflowDocument) && (workflowDocument.isApproved() || workflowDocument.isProcessed() || workflowDocument.isFinal())) {
            editModes.add(ArAuthorizationConstants.CustomerCreditMemoEditMode.DISPLAY_PRINT_BUTTON);
        }
        
        return editModes;
    }

    @Override
    public boolean canCancel(Document document) {
        return !isDocStatusCodeInitiated(document);
    }

    @Override
    public boolean canSave(Document document) {
        return !isDocStatusCodeInitiated(document);
    }
    
    /**
     * Returns true if the document passed in is in initiated status.
     */
    protected boolean isDocStatusCodeInitiated(Document document) {
        CustomerCreditMemoDocument creditMemoDoc = (CustomerCreditMemoDocument) document;
        return (StringUtils.equals(creditMemoDoc.getStatusCode(), ArConstants.CustomerCreditMemoStatuses.INITIATE));
    }

}
