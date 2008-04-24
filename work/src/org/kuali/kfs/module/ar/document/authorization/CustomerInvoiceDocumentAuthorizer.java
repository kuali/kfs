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
package org.kuali.module.ar.document.authorization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.AmountTotaling;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.core.document.authorization.TransactionalDocumentActionFlags;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.ar.ArAuthorizationConstants;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.document.CustomerInvoiceDocument;
import org.kuali.module.ar.service.InvoicePaidAppliedService;

public class CustomerInvoiceDocumentAuthorizer extends AccountingDocumentAuthorizerBase {
    
    /**
     * Display customer invoice document receivable line if system parameter is set to 
     * 
     * @see org.kuali.core.authorization.TransactionalDocumentAuthorizer#getEditMode(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.KualiUser, java.util.List, java.util.List)
     */
    @Override
    public Map getEditMode(Document document, UniversalUser user, List sourceAccountingLines, List targetAccountingLines) {
        Map editModeMap = super.getEditMode(document, user, sourceAccountingLines, targetAccountingLines);
        
        String receivableOffsetOption = SpringContext.getBean(ParameterService.class).getParameterValue(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        if( ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU.equals( receivableOffsetOption ) ){
            editModeMap.put(ArAuthorizationConstants.CustomerInvoiceDocumentEditMode.SHOW_RECEIVABLE_FAU, "TRUE");
        }

        return editModeMap;
    }    
    
    /**
     * Overrides document action flags to ensure error correction invoices cannot be copied or corrected.
     * 
     * @see org.kuali.core.document.authorization.DocumentAuthorizer#getDocumentActionFlags(Document, UniversalUser)
     */
    @Override
    public DocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        
        TransactionalDocumentActionFlags flags = (TransactionalDocumentActionFlags)super.getDocumentActionFlags(document, user);
        
        //Error correction invoices cannot be copied or error corrected
        if(((CustomerInvoiceDocument)document).isInvoiceReversal()){
            flags.setCanCopy(false);
            flags.setCanErrorCorrect(false);
        } else {
            //a normal invoice can only be error corrected if no amounts have been applied (excluding discounts)
            flags.setCanErrorCorrect(!SpringContext.getBean(InvoicePaidAppliedService.class).doesInvoiceHaveAppliedAmounts((CustomerInvoiceDocument)document));
        }
        
        return flags;
    }    

}
