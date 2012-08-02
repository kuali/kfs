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
package org.kuali.kfs.module.purap.document.authorization;

import java.util.Set;

import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.document.BulkReceivingDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.ObjectUtils;


public class BulkReceivingDocumentPresentationController extends PurchasingAccountsPayableDocumentPresentationController {
    
    @Override
    public boolean canSave(Document document) {
        if (((BulkReceivingDocument) document).getFinancialSystemDocumentHeader().getWorkflowDocument().isInitiated()) {
            return false;
        }
        return super.canSave(document);
    }

    @Override
    public boolean canCancel(Document document) {
        if (((BulkReceivingDocument) document).getFinancialSystemDocumentHeader().getWorkflowDocument().isInitiated()) {
            return false;
        }
        return super.canCancel(document);
    }

    @Override
    public boolean canClose(Document document) {
        if (((BulkReceivingDocument) document).getFinancialSystemDocumentHeader().getWorkflowDocument().isInitiated()) {
            return false;
        }
        return super.canClose(document);
    }

    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        BulkReceivingDocument bulkReceivingDocument = (BulkReceivingDocument)document;

        // if vendor has been selected from DB, certain vendor fields are not allowed to be edited
        if (ObjectUtils.isNotNull(bulkReceivingDocument.getVendorHeaderGeneratedIdentifier())) {
            editModes.add(PurapAuthorizationConstants.BulkReceivingEditMode.LOCK_VENDOR_ENTRY);
        }

        if (((BulkReceivingDocument) document).getFinancialSystemDocumentHeader().getWorkflowDocument().isInitiated()) {
            editModes.add(PurapAuthorizationConstants.BulkReceivingEditMode.DISPLAY_INIT_TAB);
        }
        
        return editModes;
    }
}
