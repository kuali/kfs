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
package org.kuali.kfs.module.purap.document.authorization;

import java.util.Map;

import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.document.BulkReceivingDocument;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentActionFlags;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase;
import org.kuali.rice.kns.authorization.AuthorizationConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

public class BulkReceivingDocumentAuthorizer extends FinancialSystemTransactionalDocumentAuthorizerBase  {

    @Override
    public boolean hasInitiateAuthorization(Document document, Person user) {
        //Any user can create this document.
        return true;
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#getEditMode(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kim.bo.Person)
     */
    @Override    
    public Map getEditMode(Document document, 
                           Person user) {
        
        BulkReceivingDocument blkRecDoc = (BulkReceivingDocument)document;
        
        Map editModeMap = super.getEditMode(document, user);

        String editMode = AuthorizationConstants.EditMode.VIEW_ONLY;        

        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        
        if (workflowDocument.stateIsInitiated() || 
            workflowDocument.stateIsSaved()) {
            if (hasInitiateAuthorization(document, user)) {
                editMode = AuthorizationConstants.EditMode.FULL_ENTRY;
            }
        }
        
        editModeMap.put(editMode, "TRUE");

        //display init tab
        BulkReceivingDocument bulkReceivingDocument = (BulkReceivingDocument) document;
        if (workflowDocument.stateIsInitiated()) {
            editModeMap.put(PurapAuthorizationConstants.BulkReceivingEditMode.DISPLAY_INIT_TAB, "TRUE");
        }
        else {
            editModeMap.put(PurapAuthorizationConstants.BulkReceivingEditMode.DISPLAY_INIT_TAB, "FALSE");
        }

        if (workflowDocument.stateIsInitiated() || 
            workflowDocument.stateIsSaved()) {
            if (ObjectUtils.isNotNull(blkRecDoc.getVendorHeaderGeneratedIdentifier())) {
                editModeMap.put(PurapAuthorizationConstants.RequisitionEditMode.LOCK_VENDOR_ENTRY, "TRUE");
            }
        }
        
        return editModeMap;
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizer#getDocumentActionFlags(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kim.bo.Person)
     */
    @Override
    public FinancialSystemTransactionalDocumentActionFlags getDocumentActionFlags(Document document, Person user) {
        FinancialSystemTransactionalDocumentActionFlags flags = super.getDocumentActionFlags(document, user);
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        if (workflowDocument.stateIsInitiated()) {
            flags.setCanSave(false);
            flags.setCanClose(true);
            flags.setCanCancel(false);
            flags.setCanDisapprove(false);
        }        

        // NEED TO REDO ANNOTATE CHECK SINCE CHANGED THE VALUE OF FLAGS
        this.setAnnotateFlag(flags);

        return flags;
    }

}

