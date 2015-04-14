/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
