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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.fp.document.CashManagementDocument;
import org.kuali.kfs.fp.document.CashReceiptDocument;
import org.kuali.kfs.fp.document.service.CashManagementService;
import org.kuali.kfs.fp.document.service.CashReceiptService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KfsAuthorizationConstants;
import org.kuali.kfs.sys.KFSConstants.CashDrawerConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentActionFlags;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase;
import org.kuali.rice.kew.dto.ValidActionsDTO;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.exception.DocumentInitiationAuthorizationException;
import org.kuali.rice.kns.exception.DocumentTypeAuthorizationException;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * DocumentAuthorizer containing authorization code for CashManagement documents
 */
public class CashManagementDocumentAuthorizer extends FinancialSystemTransactionalDocumentAuthorizerBase {
    private static Log LOG = LogFactory.getLog(CashManagementDocumentAuthorizer.class);

    /**
     * Overrides to implemented some document specific views.
     * 
     * @see org.kuali.rice.kns.authorization.DocumentAuthorizer#getEditMode(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kns.bo.user.KualiUser)
     */
    @Override
    public Map getEditMode(Document document, Person user) {
        // default is UNVIEWABLE for this doctype
        Map editModeMap = new HashMap();
        editModeMap.put(KfsAuthorizationConstants.CashManagementEditMode.UNVIEWABLE, "TRUE");

        // update editMode if possible
        CashManagementDocument cmDoc = (CashManagementDocument) document;

        
        if (KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), KIMServiceLocator.getIdentityManagementService().getGroupByName(KFSConstants.KFS_GROUP_NAMESPACE, cmDoc.getCampusCode()).getGroupId())) {
            editModeMap.clear();

            KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

            if (workflowDocument.stateIsInitiated()) {
                editModeMap.put(KfsAuthorizationConstants.CashManagementEditMode.FULL_ENTRY, "TRUE");
            }
            else if (workflowDocument.stateIsSaved()) {
                editModeMap.put(KfsAuthorizationConstants.CashManagementEditMode.FULL_ENTRY, "TRUE");

                editModeMap.put(KfsAuthorizationConstants.CashManagementEditMode.ALLOW_CANCEL_DEPOSITS, "TRUE");
                if (!cmDoc.hasFinalDeposit()) {
                    editModeMap.put(KfsAuthorizationConstants.CashManagementEditMode.ALLOW_ADDITIONAL_DEPOSITS, "TRUE");
                }
            }
            else {
                editModeMap.put(KfsAuthorizationConstants.CashManagementEditMode.VIEW_ONLY, "TRUE");
            }
        }

        return editModeMap;
    }

 // TODO fix for kim
//    /**
//     * @see org.kuali.rice.kns.document.DocumentAuthorizerBase#getDocumentActionFlags(org.kuali.rice.kns.document.Document,
//     *      org.kuali.rice.kns.bo.user.KualiUser)
//     */
//    @Override
//    public FinancialSystemTransactionalDocumentActionFlags getDocumentActionFlags(Document document, Person user) {
//        FinancialSystemTransactionalDocumentActionFlags flags = super.getDocumentActionFlags(document, user);
//
//        CashManagementDocument cmDoc = (CashManagementDocument) document;
//        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
//        ValidActionsDTO validActions = workflowDocument.getRouteHeader().getValidActions();
//
//        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
//            // CM document can only be saved (via the save button) if the CashDrawer is not closed
//            if (cmDoc.getCashDrawerStatus() == null || cmDoc.getCashDrawerStatus().equals(CashDrawerConstants.STATUS_CLOSED)) {
//                flags.setCanSave(false);
//            }
//            else {
//                flags.setCanSave(validActions.contains(KEWConstants.ACTION_TAKEN_SAVED_CD));
//            }
//
//            // CM document can only be routed if it contains a Final Deposit
//            if (!cmDoc.hasFinalDeposit() || !SpringContext.getBean(CashManagementService.class).allVerifiedCashReceiptsAreDeposited(cmDoc)) {
//                flags.setCanRoute(false);
//                flags.setCanBlanketApprove(false);
//            }
//            else {
//                flags.setCanRoute(validActions.contains(KEWConstants.ACTION_TAKEN_ROUTED_CD));
//                flags.setCanBlanketApprove(validActions.contains(KEWConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));
//            }
//
//            if (!SpringContext.getBean(CashManagementService.class).allowDocumentCancellation(cmDoc)) {
//                flags.setCanCancel(false);
//            }
//            else {
//                flags.setCanCancel(validActions.contains(KEWConstants.ACTION_TAKEN_CANCELED_CD));
//            }
//        }
//
//        if (workflowDocument.stateIsEnroute()) {
//            flags.setCanApprove(validActions.contains(KEWConstants.ACTION_TAKEN_APPROVED_CD));
//            flags.setCanDisapprove(validActions.contains(KEWConstants.ACTION_TAKEN_DENIED_CD));
//            flags.setCanFYI(validActions.contains(KEWConstants.ACTION_TAKEN_FYI_CD));
//        }
//
//        return flags;
//    }

    /**
     * This method checks that all verified cash receipts are deposited
     * 
     * @param cmDoc the cash management document to check
     * @return true if all verified cash receipts are deposited, false if otherwise
     */
    private boolean areAllVerifiedCashReceiptsDeposited(CashManagementDocument cmDoc) {
        boolean theyAre = true;
        List verifiedReceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(cmDoc.getCampusCode(), KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED);
        CashManagementService cms = SpringContext.getBean(CashManagementService.class);
        for (Object o : verifiedReceipts) {
            if (!cms.verifyCashReceiptIsDeposited(cmDoc, (CashReceiptDocument) o)) {
                theyAre = false;
                break;
            }
        }
        return theyAre;
    }
}

