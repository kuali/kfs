/*
 * Copyright 2007 The Kuali Foundation.
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

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.ar.ArAuthorizationConstants;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.module.ar.document.CustomerInvoiceDocument;

public class CustomerCreditMemoDocumentAuthorizer extends AccountingDocumentAuthorizerBase {
    
    @Override
    public Map getEditMode(Document document, UniversalUser user, List sourceAccountingLines, List targetAccountingLines) {
        
        Map<String,String> editModeMap = super.getEditMode(document, user, sourceAccountingLines, targetAccountingLines);
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument) document;
        
        if (StringUtils.equals(customerCreditMemoDocument.getStatusCode(),ArConstants.CustomerCreditMemoStatuses.INITIATE))
            editModeMap.put(ArAuthorizationConstants.CustomerCreditMemoEditMode.DISPLAY_INIT_TAB,"TRUE");
        else
            editModeMap.put(ArAuthorizationConstants.CustomerCreditMemoEditMode.DISPLAY_INIT_TAB,"FALSE");
        
        String receivableOffsetOption = SpringContext.getBean(ParameterService.class).getParameterValue(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        if( ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU.equals( receivableOffsetOption ) ){
            editModeMap.put(ArAuthorizationConstants.CustomerInvoiceDocumentEditMode.SHOW_RECEIVABLE_FAU, "TRUE");
        }
        
        return editModeMap;
    }
    
    /**
     * @see org.kuali.core.document.authorization.DocumentAuthorizer#getDocumentActionFlags(Document, UniversalUser)
     */
    @Override
    public DocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        DocumentActionFlags flags = super.getDocumentActionFlags(document, user);

        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument) document;
        if (StringUtils.equals(customerCreditMemoDocument.getStatusCode(), ArConstants.CustomerCreditMemoStatuses.INITIATE)) {
            flags.setCanSave(false);
            flags.setCanClose(true);
            flags.setCanCancel(false);
        }
        return flags;
    }
}
