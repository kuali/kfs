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
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.util.AccountsReceivableUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.kew.api.WorkflowDocument;

/**
 * Cash Control Document presentation Controller.
 */
public class CashControlDocumentPresentationController extends FinancialSystemTransactionalDocumentPresentationControllerBase {

    /**
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase#getEditModes(org.kuali.rice.krad.document.Document)
     */
    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);

        CashControlDocument cashControlDocument = (CashControlDocument) document;
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        if ((workflowDocument.isInitiated() || workflowDocument.isSaved()) && !(cashControlDocument.getElectronicPaymentClaims().size() > 0)) {
            editModes.add(ArAuthorizationConstants.CashControlDocumentEditMode.EDIT_PAYMENT_MEDIUM);
            editModes.add(ArAuthorizationConstants.CashControlDocumentEditMode.EDIT_DETAILS);
            editModes.add(ArAuthorizationConstants.CashControlDocumentEditMode.EDIT_REF_DOC_NBR);
            editModes.add(ArAuthorizationConstants.CashControlDocumentEditMode.EDIT_BANK_CODE);
            if (SpringContext.getBean(BankService.class).isBankSpecificationEnabled()) {
                editModes.add(ArAuthorizationConstants.CashControlDocumentEditMode.SHOW_BANK_CODE);
            }
        }
        else {
            if (StringUtils.isNotBlank(cashControlDocument.getBankCode())) {
                editModes.add(ArAuthorizationConstants.CashControlDocumentEditMode.SHOW_BANK_CODE);
            }
        }

        // if the document is in routing, then we have some special rules
        if (workflowDocument.isEnroute()) {

            // if doc is cash-type then payment app link always shows, once its in routing
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
                if (cashControlDocument.getGeneralLedgerPendingEntries().isEmpty()) {
                    editModes.add(ArAuthorizationConstants.CashControlDocumentEditMode.EDIT_PAYMENT_MEDIUM);
                    editModes.add(ArAuthorizationConstants.CashControlDocumentEditMode.SHOW_GENERATE_BUTTON);
                }
                Integer totalGLRecordsCreated = cashControlDocument.getGeneralLedgerEntriesPostedCount();

                if (totalGLRecordsCreated.intValue() > 0) {
                    editModes.remove(ArAuthorizationConstants.CashControlDocumentEditMode.SHOW_GENERATE_BUTTON);
                    editModes.remove(ArAuthorizationConstants.CashControlDocumentEditMode.EDIT_PAYMENT_MEDIUM);
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

    /**
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#canBlanketApprove(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canBlanketApprove(Document document) {
        return false;
    }

    /**
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#canDisapprove(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canDisapprove(Document document) {
        return !hasAtLeastOneAppDocApproved((CashControlDocument) document);
    }

    /**
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#canApprove(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canApprove(Document document) {
        return hasAllAppDocsApproved((CashControlDocument) document);
    }

    /*
     * Can correct if 1. there is at least one correctable cash control detail (payment application) 2. and cash control document is
     * enroute or final
     */
    @Override
    public boolean canErrorCorrect(FinancialSystemTransactionalDocument document) {
        return (hasAtLeastOneCorrectableCashControlDetail((CashControlDocument) document) && (document.getDocumentHeader().getWorkflowDocument().isEnroute() || document.getDocumentHeader().getWorkflowDocument().isFinal()));
    }

    /**
     * This method returns true if there is at least one correctable cash control detail.
     * 
     * @param cashControlDocument
     * @return
     */
    protected boolean hasAtLeastOneCorrectableCashControlDetail(CashControlDocument cashControlDocument) {
        for (CashControlDetail cashControlDetail : cashControlDocument.getCashControlDetails()) {
            if (AccountsReceivableUtils.canCorrectDetail(cashControlDetail)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#canCancel(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canCancel(Document document) {
        return !hasAtLeastOneAppDocApproved((CashControlDocument) document);
    }

    /**
     * @param document
     * @return
     */
    protected boolean containsGLPEs(CashControlDocument document) {
        return !document.getGeneralLedgerPendingEntries().isEmpty();
    }

    /**
     * This method checks if the CashControlDocument has at least one application document that has been approved
     * 
     * @param ccDoc the CashControlDocument
     * @return true if it has at least one application document approved, false otherwise
     */
    protected boolean hasAtLeastOneAppDocApproved(CashControlDocument cashControlDocument) {
        boolean resultInd = false;
        // check if there is at least one Application Document approved
        for (CashControlDetail cashControlDetail : cashControlDocument.getCashControlDetails()) {
            PaymentApplicationDocument applicationDocument = cashControlDetail.getReferenceFinancialDocument();
            WorkflowDocument workflowDocument = applicationDocument.getDocumentHeader().getWorkflowDocument();

            if (workflowDocument != null && workflowDocument.isApproved()) {
                resultInd = true;
                break;
            }
        }
        return resultInd;
    }

    /**
     * This method chech if all application document have been approved
     * 
     * @param cashControlDocument the CashControlDocument
     * @return true if all application documents have been approved, false otherwise
     */
    protected boolean hasAllAppDocsApproved(CashControlDocument cashControlDocument) {
        boolean resultInd = true;
        for (CashControlDetail cashControlDetail : cashControlDocument.getCashControlDetails()) {

            PaymentApplicationDocument applicationDocument = cashControlDetail.getReferenceFinancialDocument();
            WorkflowDocument workflowDocument = applicationDocument.getDocumentHeader().getWorkflowDocument();

            if (!(workflowDocument.isApproved() || workflowDocument.isFinal())) {
                resultInd = false;
                break;
            }

        }
        return resultInd;
    }

}
