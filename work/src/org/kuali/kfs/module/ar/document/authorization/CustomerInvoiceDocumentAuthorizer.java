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
package org.kuali.kfs.module.ar.document.authorization;

import java.util.Map;

import org.kuali.kfs.module.ar.ArAuthorizationConstants;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.InvoicePaidAppliedService;
import org.kuali.kfs.module.ar.util.ARUtil;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentActionFlags;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.exception.DocumentInitiationAuthorizationException;
import org.kuali.rice.kns.exception.DocumentTypeAuthorizationException;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

public class CustomerInvoiceDocumentAuthorizer extends AccountingDocumentAuthorizerBase {
    
    /**
     * Display customer invoice document receivable line if system parameter is set to 
     * 
     * @see org.kuali.rice.kns.authorization.TransactionalDocumentAuthorizer#getEditMode(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kns.bo.user.KualiUser, java.util.List, java.util.List)
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map getEditMode(Document document, Person user) {
        Map editModeMap = super.getEditMode(document, user);
        
        String receivableOffsetOption = SpringContext.getBean(ParameterService.class).getParameterValue(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        if( ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU.equals( receivableOffsetOption ) ){
            editModeMap.put(ArAuthorizationConstants.CustomerInvoiceDocumentEditMode.SHOW_RECEIVABLE_FAU, "TRUE");
        }

        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (ObjectUtils.isNotNull(workflowDocument) && (workflowDocument.stateIsApproved() || workflowDocument.stateIsProcessed() || workflowDocument.stateIsFinal())) {
            editModeMap.put(ArAuthorizationConstants.CustomerInvoiceDocumentEditMode.DISPLAY_PRINT_BUTTON, "TRUE");
        }
        else {
            editModeMap.put(ArAuthorizationConstants.CustomerInvoiceDocumentEditMode.DISPLAY_PRINT_BUTTON, "FALSE");
        }
        
        return editModeMap;
    }    
    
 // TODO fix for kim
//    /**
//     * Overrides document action flags to ensure error correction invoices cannot be copied or corrected.
//     * 
//     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#getDocumentActionFlags(Document, Person)
//     */
//    @Override
//    public FinancialSystemTransactionalDocumentActionFlags getDocumentActionFlags(Document document, Person user) {
//        FinancialSystemTransactionalDocumentActionFlags flags = super.getDocumentActionFlags(document, user);
//        
//        //Error correction invoices cannot be copied or error corrected
//        if(((CustomerInvoiceDocument)document).isInvoiceReversal()){
//            flags.setCanCopy(false);
//            flags.setCanErrorCorrect(false);
//        } else {
//            //a normal invoice can only be error corrected if document is in a final state and no amounts have been applied (excluding discounts)
//            flags.setCanErrorCorrect(document.getDocumentHeader().getWorkflowDocument().stateIsFinal() && !SpringContext.getBean(InvoicePaidAppliedService.class).doesInvoiceHaveAppliedAmounts((CustomerInvoiceDocument)document));
//        }
//        
//        return flags;
//    }    

    // TODO remove - replaced by kim
//    /**
//     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#canInitiate(java.lang.String, org.kuali.rice.kim.bo.Person)
//     */
//    @Override
//    public void canInitiate(String documentTypeName, Person user) throws DocumentTypeAuthorizationException {
//        super.canInitiate(documentTypeName, user);
//
//        if (!ARUtil.isUserInArBillingOrg(user)) {
//            throw new DocumentInitiationAuthorizationException(ArKeyConstants.ERROR_ORGANIZATION_OPTIONS_MUST_BE_SET_FOR_USER_ORG, 
//                    new String[] { "(Users in an AR Billing Org)", "Customer Invoice" });
//
//        }
//    }    

}

