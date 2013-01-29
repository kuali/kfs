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
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.InvoicePaidAppliedService;
import org.kuali.kfs.sys.KfsAuthorizationConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
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

        ParameterService paramService = SpringContext.getBean(ParameterService.class);
        String receivableOffsetOption = paramService.getParameterValueAsString(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);

        if( ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU.equals( receivableOffsetOption ) ){
            editModes.add(ArAuthorizationConstants.CustomerInvoiceDocumentEditMode.SHOW_RECEIVABLE_FAU);
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
        boolean isFinal = document.getDocumentHeader().getWorkflowDocument().isFinal();

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
