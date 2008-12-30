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
package org.kuali.kfs.module.ar.document.authorization;

import java.util.Map;

import org.kuali.kfs.module.ar.ArAuthorizationConstants;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.util.ARUtil;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentActionFlags;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.exception.DocumentInitiationAuthorizationException;
import org.kuali.rice.kns.exception.DocumentTypeAuthorizationException;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

public class CashControlDocumentAuthorizer extends FinancialSystemTransactionalDocumentAuthorizerBase {

 // TODO fix for kim - looks like presentationl controller logic not authorization

//    /**
//     * @see org.kuali.rice.kns.document.DocumentAuthorizerBase#getDocumentActionFlags(org.kuali.rice.kns.document.Document,
//     *      org.kuali.rice.kns.bo.user.KualiUser)
//     */
//    @Override
//    public FinancialSystemTransactionalDocumentActionFlags getDocumentActionFlags(Document document, Person user) {
//
//        FinancialSystemTransactionalDocumentActionFlags flags = super.getDocumentActionFlags(document, user);
//        CashControlDocument cashControlDocument = (CashControlDocument) document;
//
//        // Blanket Approval is not used for CashControlDocument
//        flags.setCanBlanketApprove(false);
//
//        // if at least one application document has been approved the Cash Control Document cannot be disapproved
//        if (hasAtLeastOneAppDocApproved(cashControlDocument)) {
//            flags.setCanDisapprove(false);
//        }
//
//        // if not all application documents have been approved the CashControlDocument cannot be approved
//        if (!hasAllAppDocsApproved(cashControlDocument)) {
//            flags.setCanApprove(false);
//        }
//        
//        //  if the document is in their action list, then they can approve it only if there are GLPEs generated 
//        // on the doc.  In reality, the 'approve' button never shows up, but the approval is done 
//        // programmatically when the GLPE's are generated.  See KULAR-465
//        //KualiWorkflowDocument workflowDocument = cashControlDocument.getDocumentHeader().getWorkflowDocument();
//        //if (workflowDocument.isApprovalRequested()) {
//        //    flags.setCanApprove(containsGLPEs(cashControlDocument));
//        //}
//        
//        return flags;
//
//    }

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
    // TODO remove - replaced by kim
//    /**
//     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#canInitiate(java.lang.String, org.kuali.rice.kim.bo.Person)
//     */
//    @Override
//    public void canInitiate(String documentTypeName, Person user) throws DocumentTypeAuthorizationException {
//        super.canInitiate(documentTypeName, user);
//
//        if (!ARUtil.isUserInArBillingOrg(user)) {
//            throw new DocumentInitiationAuthorizationException(ArKeyConstants.ERROR_ORGANIZATION_OPTIONS_MUST_BE_SET_FOR_USER_ORG, 
//                    new String[] { "(Users in an AR Billing Org)", "Cash Control" });
//        }
//    }

        @Override
    @SuppressWarnings("unchecked")
    public Map getEditMode(Document d, Person u) {
        Map editMode = super.getEditMode(d, u);
        CashControlDocument cashControlDocument = (CashControlDocument) d;
        KualiWorkflowDocument workflowDocument = d.getDocumentHeader().getWorkflowDocument();

        String editDetailsKey = ArAuthorizationConstants.CashControlDocumentEditMode.EDIT_DETAILS;
        String editPaymentMediumKey = ArAuthorizationConstants.CashControlDocumentEditMode.EDIT_PAYMENT_MEDIUM;
        String editRefDocNbrKey = ArAuthorizationConstants.CashControlDocumentEditMode.EDIT_REF_DOC_NBR;
        String editGenerateBtnKey = ArAuthorizationConstants.CashControlDocumentEditMode.SHOW_GENERATE_BUTTON;
        String editPaymentAppDoc = ArAuthorizationConstants.CashControlDocumentEditMode.EDIT_PAYMENT_APP_DOC;
        String editBankCodeKey = ArAuthorizationConstants.CashControlDocumentEditMode.EDIT_BANK_CODE;

        if ((workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) && !(cashControlDocument.getElectronicPaymentClaims().size() > 0)) {
            editMode.put(editPaymentMediumKey, "TRUE");
            editMode.put(editDetailsKey, "TRUE");
            editMode.put(editRefDocNbrKey, "TRUE");
            editMode.put(editBankCodeKey, "TRUE");
        }

        // if the document is in routing, then we have some special rules
        if (workflowDocument.stateIsEnroute()) {

            // the person who has the approval request in their Action List
            // should be able to modify the document
            if (workflowDocument.isApprovalRequested() && !ArConstants.PaymentMediumCode.CASH.equalsIgnoreCase(cashControlDocument.getCustomerPaymentMediumCode())) {
                // if glpes have not been generated yet the user can change payment medium and generate glpes
                if (cashControlDocument.getGeneralLedgerPendingEntries().isEmpty() && !(cashControlDocument.getElectronicPaymentClaims().size() > 0)) {
                    editMode.put(editPaymentMediumKey, "TRUE");
                    editMode.put(editGenerateBtnKey, "TRUE");
                }
                else if (!cashControlDocument.getGeneralLedgerPendingEntries().isEmpty()) {
                    editMode.put(editPaymentAppDoc, "TRUE");
                }
            }
            if (workflowDocument.isApprovalRequested() && ArConstants.PaymentMediumCode.CASH.equalsIgnoreCase(cashControlDocument.getCustomerPaymentMediumCode())) {
                // if payment medium cash then the ref doc number can be changed
                editMode.put(editPaymentMediumKey, "TRUE");
                editMode.put(editRefDocNbrKey, "TRUE");
                editMode.put(editPaymentAppDoc, "TRUE");
            }
        }

        return editMode;
    }

}

