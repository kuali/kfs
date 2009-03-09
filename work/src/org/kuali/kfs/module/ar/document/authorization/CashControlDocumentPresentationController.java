/*
 * Copyright 2009 The Kuali Foundation.
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

import java.util.Set;

import org.kuali.kfs.module.ar.ArAuthorizationConstants;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

public class CashControlDocumentPresentationController extends FinancialSystemTransactionalDocumentPresentationControllerBase {

    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        
        CashControlDocument cashControlDocument = (CashControlDocument) document;
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        if ((workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) && !(cashControlDocument.getElectronicPaymentClaims().size() > 0)) {
            editModes.add(ArAuthorizationConstants.CashControlDocumentEditMode.EDIT_PAYMENT_MEDIUM);
            editModes.add(ArAuthorizationConstants.CashControlDocumentEditMode.EDIT_DETAILS);
            editModes.add(ArAuthorizationConstants.CashControlDocumentEditMode.EDIT_REF_DOC_NBR);
            editModes.add(ArAuthorizationConstants.CashControlDocumentEditMode.EDIT_BANK_CODE);
        }

        // if the document is in routing, then we have some special rules
        if (workflowDocument.stateIsEnroute()) {

            //  if doc is cash-type then payment app link always shows, once its in routing
            if (ArConstants.PaymentMediumCode.CASH.equalsIgnoreCase(cashControlDocument.getCustomerPaymentMediumCode())) {
                editModes.add(ArAuthorizationConstants.CashControlDocumentEditMode.EDIT_PAYMENT_APP_DOC);
            }
            // if not cash, then payapp link only shows once the GLPE's have been generated
            else if (!cashControlDocument.getGeneralLedgerPendingEntries().isEmpty()) {
                editModes.add(ArAuthorizationConstants.CashControlDocumentEditMode.EDIT_PAYMENT_APP_DOC);
            }

            // the person who has the approval request in their Action List
            // should be able to modify the document
            if (workflowDocument.isApprovalRequested() && !ArConstants.PaymentMediumCode.CASH.equalsIgnoreCase(cashControlDocument.getCustomerPaymentMediumCode())) {
                // if glpes have not been generated yet the user can change payment medium and generate glpes
                if (cashControlDocument.getGeneralLedgerPendingEntries().isEmpty() && !(cashControlDocument.getElectronicPaymentClaims().size() > 0)) {
                    editModes.add(ArAuthorizationConstants.CashControlDocumentEditMode.EDIT_PAYMENT_MEDIUM);
                    editModes.add(ArAuthorizationConstants.CashControlDocumentEditMode.SHOW_GENERATE_BUTTON);
                }
            }
            if (workflowDocument.isApprovalRequested() && ArConstants.PaymentMediumCode.CASH.equalsIgnoreCase(cashControlDocument.getCustomerPaymentMediumCode())) {
                // if payment medium cash then the ref doc number can be changed
                editModes.add(ArAuthorizationConstants.CashControlDocumentEditMode.EDIT_PAYMENT_MEDIUM);
                editModes.add(ArAuthorizationConstants.CashControlDocumentEditMode.EDIT_REF_DOC_NBR);
            }
        }

        return editModes;
    }

    @Override
    public boolean canBlanketApprove(Document document) {
        return false;
    }
    
    @Override
    public boolean canDisapprove(Document document) {
        return !hasAtLeastOneAppDocApproved((CashControlDocument) document);
    }
    
    @Override
    public boolean canApprove(Document document) {
        return hasAllAppDocsApproved((CashControlDocument) document);
    }
    
    @Override
    public boolean canErrorCorrect(FinancialSystemTransactionalDocument document) {
        return false;
    }

    @Override
    public boolean canCancel(Document document) {
        return !hasAtLeastOneAppDocApproved((CashControlDocument) document);
    }
    
    private boolean containsGLPEs(CashControlDocument document) {
        return !document.getGeneralLedgerPendingEntries().isEmpty();
    }

    /**
     * This method checks if the CashControlDocument has at least one application document that has been approved
     * 
     * @param ccDoc the CashControlDocument
     * @return true if it has at least one application document approved, false otherwise
     */
    private boolean hasAtLeastOneAppDocApproved(CashControlDocument cashControlDocument) {
        boolean result = false;
        // check if there is at least one Application Document approved
        for (CashControlDetail cashControlDetail : cashControlDocument.getCashControlDetails()) {
            PaymentApplicationDocument applicationDocument = cashControlDetail.getReferenceFinancialDocument();
            KualiWorkflowDocument workflowDocument = applicationDocument.getDocumentHeader().getWorkflowDocument();

            if (workflowDocument != null && workflowDocument.stateIsApproved()) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * This method chech if all application document have been approved
     * 
     * @param cashControlDocument the CashControlDocument
     * @return true if all application documents have been approved, false otherwise
     */
    private boolean hasAllAppDocsApproved(CashControlDocument cashControlDocument) {
        boolean result = true;
        for (CashControlDetail cashControlDetail : cashControlDocument.getCashControlDetails()) {

            PaymentApplicationDocument applicationDocument = cashControlDetail.getReferenceFinancialDocument();
            KualiWorkflowDocument workflowDocument = applicationDocument.getDocumentHeader().getWorkflowDocument();

            if (!(workflowDocument.stateIsApproved() || workflowDocument.stateIsFinal())) {
                result = false;
                break;
            }

        }
        return result;
    }

}
