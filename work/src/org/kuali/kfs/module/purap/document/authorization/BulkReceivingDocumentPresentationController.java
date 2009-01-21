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
package org.kuali.kfs.module.purap.document.authorization;

import java.util.HashSet;
import java.util.Set;

import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.document.BulkReceivingDocument;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;


public class BulkReceivingDocumentPresentationController extends FinancialSystemTransactionalDocumentPresentationControllerBase {
    
    @Override
    protected boolean canSave(Document document) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.stateIsInitiated()) {
            return false;
        }
        return super.canSave(document);
    }

    @Override
    protected boolean canCancel(Document document) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.stateIsInitiated()) {
            return false;
        }
        return super.canCancel(document);
    }

    @Override
    protected boolean canClose(Document document) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.stateIsInitiated()) {
            return false;
        }
        return super.canClose(document);
    }

    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        BulkReceivingDocument bulkReceivingDocument = (BulkReceivingDocument)document;

        // if vendor has been selected from DB, certain vendor fields are not allowed to be edited
        if (ObjectUtils.isNotNull(bulkReceivingDocument.getVendorHeaderGeneratedIdentifier())) {
            editModes.add(PurapAuthorizationConstants.BulkReceivingEditMode.LOCK_VENDOR_ENTRY);
        }

        if (workflowDocument.stateIsInitiated()) {
            editModes.add(PurapAuthorizationConstants.BulkReceivingEditMode.DISPLAY_INIT_TAB);
        }
        
        return editModes;
    }
    
}
