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

import java.util.HashMap;
import java.util.Map;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.core.document.authorization.TransactionalDocumentAuthorizerBase;
import org.kuali.core.exceptions.DocumentInitiationAuthorizationException;
import org.kuali.core.exceptions.DocumentTypeAuthorizationException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.ar.ArAuthorizationConstants;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.CashControlDetail;
import org.kuali.module.ar.bo.OrganizationOptions;
import org.kuali.module.ar.document.CashControlDocument;
import org.kuali.module.ar.document.PaymentApplicationDocument;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.chart.lookup.valuefinder.ValueFinderUtil;

public class CashControlDocumentAuthorizer extends TransactionalDocumentAuthorizerBase {

    /**
     * @see org.kuali.core.document.DocumentAuthorizerBase#getDocumentActionFlags(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.KualiUser)
     */
    @Override
    public DocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {

        DocumentActionFlags flags = super.getDocumentActionFlags(document, user);
        CashControlDocument cashControlDocument = (CashControlDocument) document;

        // Blanket Approval is not used for CashControlDocument
        flags.setCanBlanketApprove(false);

        // if at least one application document has been approved the Cash Control Document cannot be disapproved
        if (hasAtLeastOneAppDocApproved(cashControlDocument)) {
            flags.setCanDisapprove(false);
        }

        // if not all application documents have been approved the CashControlDocument cannot be approved
        if (!hasAllAppDocsApproved(cashControlDocument)) {
            flags.setCanApprove(false);
        }

        return flags;

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

    /**
     * @see org.kuali.core.document.authorization.DocumentAuthorizerBase#canInitiate(java.lang.String, org.kuali.core.bo.user.UniversalUser)
     */
    @Override
    public void canInitiate(String documentTypeName, UniversalUser user) throws DocumentTypeAuthorizationException {
        super.canInitiate(documentTypeName, user);
        // to initiate, the user must have the organization options set up.
        ChartUser chartUser = ValueFinderUtil.getCurrentChartUser();
        String chartCode = chartUser.getChartOfAccountsCode();
        String orgCode = chartUser.getUserOrganizationCode();

        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("chartOfAccountsCode", chartCode);
        criteria.put("organizationCode", orgCode);
        OrganizationOptions organizationOptions = (OrganizationOptions) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OrganizationOptions.class, criteria);

        KualiConfigurationService configurationService = SpringContext.getBean(KualiConfigurationService.class);

        //if organization doesn't exist
        if (ObjectUtils.isNull(organizationOptions)) {
            throw new DocumentInitiationAuthorizationException(ArConstants.ERROR_ORGANIZATION_OPTIONS_MUST_BE_SET_FOR_USER_ORG, new String[] {});

        }
    }

    @Override
    public Map getEditMode(Document d, UniversalUser u) {
        Map editMode = super.getEditMode(d, u);
        CashControlDocument cashControlDocument = (CashControlDocument) d;
        KualiWorkflowDocument workflowDocument = d.getDocumentHeader().getWorkflowDocument();

        String editDetailsKey = ArAuthorizationConstants.CashControlDocumentEditMode.EDIT_DETAILS;
        String editPaymentMediumKey = ArAuthorizationConstants.CashControlDocumentEditMode.EDIT_PAYMENT_MEDIUM;
        String editRefDocNbrKey = ArAuthorizationConstants.CashControlDocumentEditMode.EDIT_REF_DOC_NBR;
        String editGenerateBtnKey = ArAuthorizationConstants.CashControlDocumentEditMode.SHOW_GENERATE_BUTTON;
        String editPaymentAppDoc = ArAuthorizationConstants.CashControlDocumentEditMode.EDIT_PAYMENT_APP_DOC;

        if ((workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) && !(cashControlDocument.getElectronicPaymentClaims().size() > 0)) {
            editMode.put(editPaymentMediumKey, "TRUE");
            editMode.put(editDetailsKey, "TRUE");
            editMode.put(editRefDocNbrKey, "TRUE");
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
