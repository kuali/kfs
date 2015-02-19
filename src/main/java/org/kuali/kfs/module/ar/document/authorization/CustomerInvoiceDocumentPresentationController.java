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
package org.kuali.kfs.module.ar.document.authorization;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArAuthorizationConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.InvoicePaidAppliedService;
import org.kuali.kfs.sys.KfsAuthorizationConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

public class CustomerInvoiceDocumentPresentationController extends FinancialSystemTransactionalDocumentPresentationControllerBase {


    @Override
   public Set<String> getDocumentActions(Document document) {

        Set<String> documentActions = super.getDocumentActions(document);
        if (isDocErrorCorrectionMode((FinancialSystemTransactionalDocument) document)) {
            documentActions.remove(KRADConstants.KUALI_ACTION_CAN_EDIT);
        }

        return documentActions;
    }

    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);

        if (!isDocErrorCorrectionMode((FinancialSystemTransactionalDocument) document)) {
            editModes.add(KfsAuthorizationConstants.CustomerInvoiceEditMode.PROCESSING_ORGANIZATION_MODE);

        }

        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (ObjectUtils.isNotNull(workflowDocument) && (workflowDocument.isApproved() || workflowDocument.isProcessed() || workflowDocument.isFinal())) {
            editModes.add(ArAuthorizationConstants.CustomerInvoiceDocumentEditMode.DISPLAY_PRINT_BUTTON);
        }
        else {
            if (editModes.contains(ArAuthorizationConstants.CustomerInvoiceDocumentEditMode.DISPLAY_PRINT_BUTTON)) {
                editModes.remove(ArAuthorizationConstants.CustomerInvoiceDocumentEditMode.DISPLAY_PRINT_BUTTON);
            }
        }

        if (ObjectUtils.isNotNull(workflowDocument) && workflowDocument.isEnroute()) {
            editModes.add(ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_ITEM_DESCRIPTION);
        }

        return editModes;
    }

    @Override
    public boolean canCopy(Document document) {
        boolean copyable = true;
        CustomerInvoiceDocument ciDoc = (CustomerInvoiceDocument)document;

        // Confirm doc is in a saved and copyable state.
        copyable &= !ciDoc.getDocumentHeader().getWorkflowDocument().isInitiated();
        copyable &= !ciDoc.getDocumentHeader().getWorkflowDocument().isCanceled();

        // Confirm doc is reversible.
        copyable &= !((CustomerInvoiceDocument)document).isInvoiceReversal();
        return copyable;
    }

    @Override
    public boolean canErrorCorrect(FinancialSystemTransactionalDocument document) {
        // check if this document has been error corrected
        if (StringUtils.isNotBlank(document.getFinancialSystemDocumentHeader().getCorrectedByDocumentId())) {
            return false;
        }

        // error correction shouldn't be allowed for previous FY docs
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (!isApprovalDateWithinFiscalYear(workflowDocument)) {
            return false;
        }

        if(((CustomerInvoiceDocument)document).isInvoiceReversal()){
            return false;
        } else {
            // a normal invoice can only be error corrected if document is in a final state
            // and no amounts have been applied (excluding discounts)
            return isDocFinalWithNoAppliedAmountsExceptDiscounts((CustomerInvoiceDocument) document);
        }
    }

    //  if this isnt self-explanatory, I dont know what is
    protected boolean isDocFinalWithNoAppliedAmountsExceptDiscounts(CustomerInvoiceDocument document) {
        boolean isFinal = (document.getDocumentHeader().getWorkflowDocument().isFinal() || document.getDocumentHeader().getWorkflowDocument().isProcessed() );

        InvoicePaidAppliedService<CustomerInvoiceDetail> paidAppliedService = SpringContext.getBean(InvoicePaidAppliedService.class);
        boolean hasAppliedAmountsExcludingDiscounts = paidAppliedService.doesInvoiceHaveAppliedAmounts(document);

        return isFinal && !hasAppliedAmountsExcludingDiscounts;
    }

    protected boolean isDocErrorCorrectionMode(FinancialSystemTransactionalDocument document) {

        // check if this document has been error corrected
        if (StringUtils.isNotBlank(document.getFinancialSystemDocumentHeader().getCorrectedByDocumentId())) {
            return true;
        }

        if(((CustomerInvoiceDocument)document).isInvoiceReversal()){
            return true;
        } else {
            // a normal invoice can only be error corrected if document is in a final state
            // and no amounts have been applied (excluding discounts)
            return isDocFinalWithNoAppliedAmountsExceptDiscounts((CustomerInvoiceDocument) document);
        }
    }

}
