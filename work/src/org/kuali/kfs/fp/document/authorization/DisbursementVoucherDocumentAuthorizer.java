/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.financial.document.authorization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.Constants;
import org.kuali.PropertyConstants;
import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.bo.user.KualiGroup;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.core.document.authorization.TransactionalDocumentActionFlags;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.module.financial.rules.DisbursementVoucherRuleConstants;
import org.kuali.workflow.KualiWorkflowUtils.RouteLevelNames;

/**
 * Document Authorizer for the Disbursement Voucher document.
 */
public class DisbursementVoucherDocumentAuthorizer extends AccountingDocumentAuthorizerBase {

    private static String taxGroupName;
    private static String travelGroupName;
    private static String wireTransferGroupName;
    private static String frnGroupName;
    private static String adminGroupName;
    
    /**
     * Overrides to call super and then blanketly set the canBlanketApprove flag to false, since this is never allowed on a DV.
     * 
     * @see org.kuali.core.authorization.DocumentAuthorizer#getDocumentActionFlags(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.KualiUser)
     */
    public DocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        TransactionalDocumentActionFlags flags = new TransactionalDocumentActionFlags(super.getDocumentActionFlags(document, user));

        flags.setCanBlanketApprove(false); // this is never allowed on a DV document

        flags.setCanErrorCorrect(false); // CR, DV, andd PCDO don't allow error correction

        return flags;
    }

    /**
     * @see org.kuali.core.authorization.DocumentAuthorizer#getEditMode(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.KualiUser)
     */
    public Map getEditMode(Document document, UniversalUser user, List sourceLines, List targetLines) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        Map editModeMap = super.getEditMode(document, user, sourceLines, targetLines);
        if (((workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) && workflowDocument.userIsInitiator(user)) || (workflowDocument.stateIsEnroute() && workflowDocument.isApprovalRequested() && isSpecialRouting(document, user))) {
            setDVWorkgroupEditModes(editModeMap, document, user);
        }

        return editModeMap;
    }

    /**
     * Sets additional edit modes based on the user's workgroups.
     * 
     * @param editModeMap
     * @param document
     * @param user
     */
    private void setDVWorkgroupEditModes(Map editModeMap, Document document, UniversalUser user) {
        if ( isUserInTaxGroup( user ) ) {
            editModeMap.put(AuthorizationConstants.DisbursementVoucherEditMode.TAX_ENTRY, "TRUE");
            editModeMap.put(AuthorizationConstants.DisbursementVoucherEditMode.EXPENSE_SPECIAL_ENTRY, "TRUE");
        }
        if ( isUserInFRNGroup( user ) ) {
            editModeMap.put(AuthorizationConstants.DisbursementVoucherEditMode.FRN_ENTRY, "TRUE");
            editModeMap.put(AuthorizationConstants.DisbursementVoucherEditMode.EXPENSE_SPECIAL_ENTRY, "TRUE");
        }
        if ( isUserInTravelGroup( user ) ) {
            editModeMap.put(AuthorizationConstants.DisbursementVoucherEditMode.TRAVEL_ENTRY, "TRUE");
            editModeMap.put(AuthorizationConstants.DisbursementVoucherEditMode.EXPENSE_SPECIAL_ENTRY, "TRUE");
        }
        if ( isUserInWireGroup( user ) ) {
            editModeMap.put(AuthorizationConstants.DisbursementVoucherEditMode.WIRE_ENTRY, "TRUE");
            editModeMap.put(AuthorizationConstants.DisbursementVoucherEditMode.EXPENSE_SPECIAL_ENTRY, "TRUE");
        }
        if ( isUserInDvAdminGroup( user ) ) {
            editModeMap.put(AuthorizationConstants.DisbursementVoucherEditMode.ADMIN_ENTRY, "TRUE");
            editModeMap.put(AuthorizationConstants.DisbursementVoucherEditMode.EXPENSE_SPECIAL_ENTRY, "TRUE");
        }
    }

    /**
     * Checks if the current user is a member of the dv tax workgroup.
     * 
     * @return true if user is in group
     */
    private boolean isUserInTaxGroup( UniversalUser user ) {
        if ( taxGroupName == null ) {
            taxGroupName = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue( Constants.FinancialApcParms.GROUP_DV_DOCUMENT, Constants.FinancialApcParms.DV_TAX_WORKGROUP );
        }
        return user.isMember( taxGroupName );
    }

    /**
     * Checks if the current user is a member of the dv travel workgroup.
     * 
     * @return true if user is in group
     */
    private boolean isUserInTravelGroup( UniversalUser user ) {
        if ( travelGroupName == null ) {
            travelGroupName = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue( Constants.FinancialApcParms.GROUP_DV_DOCUMENT, Constants.FinancialApcParms.DV_TRAVEL_WORKGROUP );
        }
        return user.isMember( travelGroupName );
    }

    /**
     * Checks if the current user is a member of the dv frn workgroup.
     * 
     * @return true if user is in group
     */
    private boolean isUserInFRNGroup( UniversalUser user ) {
        if ( frnGroupName == null ) {
            frnGroupName = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue( Constants.FinancialApcParms.GROUP_DV_DOCUMENT, Constants.FinancialApcParms.DV_FOREIGNDRAFT_WORKGROUP );
        }
        return user.isMember( frnGroupName );
    }

    /**
     * Checks if the current user is a member of the dv wire workgroup.
     * 
     * @return true if user is in group
     */
    private boolean isUserInWireGroup( UniversalUser user ) {
        if ( wireTransferGroupName == null ) {
            wireTransferGroupName = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue( Constants.FinancialApcParms.GROUP_DV_DOCUMENT, Constants.FinancialApcParms.DV_WIRETRANSFER_WORKGROUP );
        }
        return user.isMember( wireTransferGroupName );
    }

    /**
     * This method checks to see whether the user is in the dv admin group or not.
     * 
     * @return true if user is in group, false otherwise
     */
    private boolean isUserInDvAdminGroup( UniversalUser user ) {
        if ( adminGroupName == null ) {
            adminGroupName = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue( Constants.FinancialApcParms.GROUP_DV_DOCUMENT, Constants.FinancialApcParms.DV_ADMIN_WORKGROUP );
        }
        return user.isMember( adminGroupName );
    }

    /**
     * @see org.kuali.core.authorization.DocumentAuthorizer#getAccountingLineEditableFields(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.KualiUser)
     */
    public Map getAccountingLineEditableFields(Document document, UniversalUser user) {
        Map editableFields = new HashMap();
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        // only check special expense edits if we are in special routing
        if (!workflowDocument.stateIsEnroute() || !workflowDocument.isApprovalRequested() || !isSpecialRouting(document, user)) {
            return editableFields;
        }

        if ( isUserInDvAdminGroup( user ) || isUserInTravelGroup( user ) ) {
            // retrieve allow object code edit indicator
            boolean allowObjectEdits = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterIndicator(DisbursementVoucherRuleConstants.DV_DOCUMENT_PARAMETERS_GROUP_NM, DisbursementVoucherRuleConstants.ALLOW_OBJECT_CODE_EDITS);
            if (allowObjectEdits) {
                editableFields.put(PropertyConstants.FINANCIAL_OBJECT_CODE, "TRUE");
            }
        }

        if ( isUserInTaxGroup( user ) || isUserInFRNGroup( user ) || isUserInWireGroup( user ) || isUserInTravelGroup( user ) ) {
            editableFields.put(PropertyConstants.AMOUNT, "TRUE");
        }

        return editableFields;
    }

    /**
     * Determines if the current active routing nodes are one of the disbursement voucher special routing nodes.
     * 
     * @param document
     * @param user
     * @return boolean
     */
    public boolean isSpecialRouting(Document document, UniversalUser user) {
        boolean isSpecialRouteNode = false;

        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        List activeNodes = getCurrentRouteLevels(workflowDocument);

        List dvSpecialNodes = new ArrayList();
        dvSpecialNodes.add(RouteLevelNames.ALIEN_INDICATOR);
        dvSpecialNodes.add(RouteLevelNames.ALIEN_INDICATOR_PAYMENT_REASON);
        dvSpecialNodes.add(RouteLevelNames.CAMPUS_CODE);
        dvSpecialNodes.add(RouteLevelNames.EMPLOYEE_INDICATOR);
        dvSpecialNodes.add(RouteLevelNames.PAYMENT_METHOD);
        dvSpecialNodes.add(RouteLevelNames.PAYMENT_REASON);
        dvSpecialNodes.add(RouteLevelNames.PAYMENT_REASON_CAMPUS);
        dvSpecialNodes.add(RouteLevelNames.TAX_CONTROL_CODE);

        if (CollectionUtils.containsAny(activeNodes, dvSpecialNodes)) {
            isSpecialRouteNode = true;
        }

        return isSpecialRouteNode;
    }
}