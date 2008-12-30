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
package org.kuali.kfs.fp.document.authorization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.KfsAuthorizationConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentActionFlags;
import org.kuali.kfs.sys.document.workflow.KualiWorkflowUtils.RouteLevelNames;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * Document Authorizer for the Disbursement Voucher document.
 */
public class DisbursementVoucherDocumentAuthorizer extends AccountingDocumentAuthorizerBase {

    private static String taxGroupName;
    private static String travelGroupName;
    private static String wireTransferGroupName;
    private static String frnGroupName;
    private static String adminGroupName;

// TODO fix for kim
    //    /**
//     * Overrides to call super and then blanketly set the canBlanketApprove flag to false, since this is never allowed on a DV.
//     * 
//     * @see org.kuali.rice.kns.authorization.DocumentAuthorizer#getDocumentActionFlags(org.kuali.rice.kns.document.Document,
//     *      org.kuali.rice.kns.bo.user.KualiUser)
//     */
//    @Override
//    public FinancialSystemTransactionalDocumentActionFlags getDocumentActionFlags(Document document, Person user) {
//        FinancialSystemTransactionalDocumentActionFlags flags = new FinancialSystemTransactionalDocumentActionFlags(super.getDocumentActionFlags(document, user));
//
//        flags.setCanBlanketApprove(false); // this is never allowed on a DV document
//
//        flags.setCanErrorCorrect(false); // CR, DV, and PCDO don't allow error correction
//
//        return flags;
//    }

    /**
     * @see org.kuali.rice.kns.authorization.DocumentAuthorizer#getEditMode(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kns.bo.user.KualiUser)
     */
    @Override
    public Map getEditMode(Document document, Person user) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        Map editModeMap = super.getEditMode(document, user);
        
        DisbursementVoucherDocument dvDoc = (DisbursementVoucherDocument) document;
        
        if ((workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) && workflowDocument.userIsInitiator(user)) {
            editModeMap.put(KfsAuthorizationConstants.DisbursementVoucherEditMode.PAYEE_EDIT_MODE, "TRUE");
            setDVWorkgroupEditModes(editModeMap, document, user);
        } 
        else if(workflowDocument.stateIsEnroute() && workflowDocument.isApprovalRequested()) {
            if(editModeMap.containsKey(KfsAuthorizationConstants.TransactionalEditMode.EXPENSE_ENTRY)) {
                editModeMap.put(KfsAuthorizationConstants.DisbursementVoucherEditMode.PAYEE_EDIT_MODE, "TRUE");
            } else {
                List currentRouteLevels = getCurrentRouteLevels(workflowDocument);
                if (currentRouteLevels.contains(RouteLevelNames.ORG_REVIEW)) {
                    editModeMap.put(KfsAuthorizationConstants.DisbursementVoucherEditMode.PAYEE_EDIT_MODE, "TRUE");
                }
            }
            if(isSpecialRouting(document, user)) {
                setDVWorkgroupEditModes(editModeMap, document, user);
            }
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
    private void setDVWorkgroupEditModes(Map editModeMap, Document document, Person user) {
        if (isUserInTaxGroup(user)) {
            editModeMap.put(KfsAuthorizationConstants.DisbursementVoucherEditMode.TAX_ENTRY, "TRUE");
            editModeMap.put(KfsAuthorizationConstants.DisbursementVoucherEditMode.EXPENSE_SPECIAL_ENTRY, "TRUE");
        }
        if (isUserInFRNGroup(user)) {
            editModeMap.put(KfsAuthorizationConstants.DisbursementVoucherEditMode.FRN_ENTRY, "TRUE");
            editModeMap.put(KfsAuthorizationConstants.DisbursementVoucherEditMode.EXPENSE_SPECIAL_ENTRY, "TRUE");
        }
        if (isUserInTravelGroup(user)) {
            editModeMap.put(KfsAuthorizationConstants.DisbursementVoucherEditMode.TRAVEL_ENTRY, "TRUE");
            editModeMap.put(KfsAuthorizationConstants.DisbursementVoucherEditMode.EXPENSE_SPECIAL_ENTRY, "TRUE");
        }
        if (isUserInWireGroup(user)) {
            editModeMap.put(KfsAuthorizationConstants.DisbursementVoucherEditMode.WIRE_ENTRY, "TRUE");
            editModeMap.put(KfsAuthorizationConstants.DisbursementVoucherEditMode.EXPENSE_SPECIAL_ENTRY, "TRUE");
        }
        if (isUserInDvAdminGroup(user)) {
            editModeMap.put(KfsAuthorizationConstants.DisbursementVoucherEditMode.ADMIN_ENTRY, "TRUE");
            editModeMap.put(KfsAuthorizationConstants.DisbursementVoucherEditMode.EXPENSE_SPECIAL_ENTRY, "TRUE");
        }
    }

    /**
     * Checks if the current user is a member of the dv tax workgroup.
     * 
     * @return true if user is in group
     */
    private boolean isUserInTaxGroup(Person user) {
        if (taxGroupName == null) {
            taxGroupName = SpringContext.getBean(ParameterService.class).getParameterValue(DisbursementVoucherDocument.class, KFSConstants.FinancialApcParms.DV_TAX_WORKGROUP);
        }
        return KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, taxGroupName);
    }

    /**
     * Checks if the current user is a member of the dv travel workgroup.
     * 
     * @return true if user is in group
     */
    private boolean isUserInTravelGroup(Person user) {
        if (travelGroupName == null) {
            travelGroupName = SpringContext.getBean(ParameterService.class).getParameterValue(DisbursementVoucherDocument.class, KFSConstants.FinancialApcParms.DV_TRAVEL_WORKGROUP);
        }
        return KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, travelGroupName);
    }

    /**
     * Checks if the current user is a member of the dv frn workgroup.
     * 
     * @return true if user is in group
     */
    private boolean isUserInFRNGroup(Person user) {
        if (frnGroupName == null) {
            frnGroupName = SpringContext.getBean(ParameterService.class).getParameterValue(DisbursementVoucherDocument.class, KFSConstants.FinancialApcParms.DV_FOREIGNDRAFT_WORKGROUP);
        }
        return KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, frnGroupName);
    }

    /**
     * Checks if the current user is a member of the dv wire workgroup.
     * 
     * @return true if user is in group
     */
    private boolean isUserInWireGroup(Person user) {
        if (wireTransferGroupName == null) {
            wireTransferGroupName = SpringContext.getBean(ParameterService.class).getParameterValue(DisbursementVoucherDocument.class, KFSConstants.FinancialApcParms.DV_WIRETRANSFER_WORKGROUP);
        }
        return KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, wireTransferGroupName);
    }

    /**
     * This method checks to see whether the user is in the dv admin group or not.
     * 
     * @return true if user is in group, false otherwise
     */
    private boolean isUserInDvAdminGroup(Person user) {
        if (adminGroupName == null) {
            adminGroupName = SpringContext.getBean(ParameterService.class).getParameterValue(DisbursementVoucherDocument.class, KFSConstants.FinancialApcParms.DV_ADMIN_WORKGROUP);
        }
        return KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, adminGroupName);
    }

    /**
     * Determines if the current active routing nodes are one of the disbursement voucher special routing nodes.
     * 
     * @param document
     * @param user
     * @return boolean
     */
    public boolean isSpecialRouting(Document document, Person user) {
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

