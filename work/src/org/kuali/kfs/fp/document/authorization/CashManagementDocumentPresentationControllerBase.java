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
package org.kuali.kfs.fp.document.authorization;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.CashDrawer;
import org.kuali.kfs.fp.document.CashManagementDocument;
import org.kuali.kfs.fp.document.service.CashManagementService;
import org.kuali.kfs.fp.service.CashDrawerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KfsAuthorizationConstants;
import org.kuali.kfs.sys.KFSConstants.CashDrawerConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.LedgerPostingDocumentPresentationControllerBase;
import org.kuali.rice.kew.dto.ValidActionsDTO;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.MaintenanceDocumentService;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

public class CashManagementDocumentPresentationControllerBase extends LedgerPostingDocumentPresentationControllerBase implements CashManagementDocumentPresentationController {

    /**
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase#getEditModes(org.kuali.rice.kns.document.Document)
     */
    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);

        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.stateIsSaved()) {
            editModes.add(KfsAuthorizationConstants.CashManagementEditMode.ALLOW_CANCEL_DEPOSITS);

            CashManagementDocument cashManagementDocument = (CashManagementDocument) document;
            if (!cashManagementDocument.hasFinalDeposit()) {
                editModes.add(KfsAuthorizationConstants.CashManagementEditMode.ALLOW_ADDITIONAL_DEPOSITS);
            }
        }

        return editModes;
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canApprove(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean canApprove(Document document) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.stateIsEnroute()) {
            ValidActionsDTO validActions = workflowDocument.getRouteHeader().getValidActions();
            return validActions.contains(KEWConstants.ACTION_TAKEN_APPROVED_CD);
        }

        return super.canApprove(document);
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canBlanketApprove(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean canBlanketApprove(Document document) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            CashManagementDocument cmDoc = (CashManagementDocument) document;
            if (!cmDoc.hasFinalDeposit() || !SpringContext.getBean(CashManagementService.class).allVerifiedCashReceiptsAreDeposited(cmDoc)) {
                return false;
            }

            // CM document can only be routed if it contains a Final Deposit
            ValidActionsDTO validActions = workflowDocument.getRouteHeader().getValidActions();
            return validActions.contains(KEWConstants.ACTION_TAKEN_BLANKET_APPROVE_CD);
        }

        return super.canBlanketApprove(document);
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canCancel(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean canCancel(Document document) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            CashManagementDocument cmDoc = (CashManagementDocument) document;
            if (!SpringContext.getBean(CashManagementService.class).allowDocumentCancellation(cmDoc)) {
                return false;
            }

            // CM document can only be routed if it contains a Final Deposit
            ValidActionsDTO validActions = workflowDocument.getRouteHeader().getValidActions();
            return validActions.contains(KEWConstants.ACTION_TAKEN_CANCELED_CD);
        }

        return super.canCancel(document);
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canDisapprove(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean canDisapprove(Document document) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.stateIsEnroute()) {
            ValidActionsDTO validActions = workflowDocument.getRouteHeader().getValidActions();
            return validActions.contains(KEWConstants.ACTION_TAKEN_DENIED_CD);
        }

        return super.canDisapprove(document);
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canRoute(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean canRoute(Document document) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            CashManagementDocument cmDoc = (CashManagementDocument) document;
            if (!cmDoc.hasFinalDeposit() || !SpringContext.getBean(CashManagementService.class).allVerifiedCashReceiptsAreDeposited(cmDoc)) {
                return false;
            }

            // CM document can only be routed if it contains a Final Deposit
            ValidActionsDTO validActions = workflowDocument.getRouteHeader().getValidActions();
            return validActions.contains(KEWConstants.ACTION_TAKEN_ROUTED_CD);
        }

        return super.canRoute(document);
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canSave(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean canSave(Document document) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            CashManagementDocument cmDoc = (CashManagementDocument) document;
            if (cmDoc.getCashDrawerStatus() == null || cmDoc.getCashDrawerStatus().equals(CashDrawerConstants.STATUS_CLOSED)) {
                return false;
            }

            // CM document can only be saved (via the save button) if the CashDrawer is not closed
            ValidActionsDTO validActions = workflowDocument.getRouteHeader().getValidActions();
            return validActions.contains(KEWConstants.ACTION_TAKEN_SAVED_CD);
        }

        return super.canRoute(document);
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canAdHocRoute(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean canAddAdhocRequests(Document document) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.stateIsEnroute()) {
            ValidActionsDTO validActions = workflowDocument.getRouteHeader().getValidActions();
            return validActions.contains(KEWConstants.ACTION_TAKEN_FYI_CD);
        }

        return super.canAddAdhocRequests(document);
    }
    
    /**
     * Determines if the cash drawer can be opened by testing two things:
     * <ol>
     *  <li>That the cash drawer is currently closed.</li>
     *  <li>That no cash drawer maintenance documents have a lock on the cash drawer.</li>
     * </ol>
     * @param document the document that wishes to open the cash drawer
     * @return true if the cash drawer can be opened, false otherwise
     */
    public boolean canOpenCashDrawer(Document document) {
        final CashDrawer cashDrawer = retrieveCashDrawer(document);
        return isCashDrawerClosed(cashDrawer) && noExistCashDrawerMaintLocks(cashDrawer, document.getDocumentNumber());
    }
    
    /**
     * Retrieves the cash drawer associated with the given cash management document
     * @param document a CashManagementDocument with an associated cash drawer
     * @return the associated cash drawer
     */
    protected CashDrawer retrieveCashDrawer(Document document) {
        final CashManagementDocument cmDoc = (CashManagementDocument)document;
        final CashDrawer cashDrawer = SpringContext.getBean(CashDrawerService.class).getByCampusCode(cmDoc.getCampusCode());
        return cashDrawer;
    }
    
    /**
     * Determines if the cash drawer's status is closed
     * @param cashDrawer the cash drawer to check
     * @return true if the given cash drawer's status is closed, false otherwise
     */
    protected boolean isCashDrawerClosed(CashDrawer cashDrawer) {
        return cashDrawer.getStatusCode().equals(KFSConstants.CashDrawerConstants.STATUS_CLOSED);
    }
    
    /**
     * Determines that no maintenance documents have locks on the given cash drawer
     * @param cashDrawer the cash drawer that may have locks on it
     * @return true if there are no maintenance documents with locks on the cash drawer, false otherwise
     */
    protected boolean noExistCashDrawerMaintLocks(CashDrawer cashDrawer, String documentNumber) {
       final MaintenanceDocumentEntry cashDrawerMaintDocEntry = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getMaintenanceDocumentEntryForBusinessObjectClass(cashDrawer.getClass());
       Maintainable cashDrawerMaintainable = createCashDrawerMaintainable(cashDrawerMaintDocEntry);
       cashDrawerMaintainable.setBoClass(cashDrawer.getClass());
       cashDrawerMaintainable.setBusinessObject(cashDrawer);
       cashDrawerMaintainable.setDocumentNumber(documentNumber);
       
       final String lockingDocument = SpringContext.getBean(MaintenanceDocumentService.class).getLockingDocumentId(cashDrawerMaintainable, documentNumber);
       return StringUtils.isBlank(lockingDocument);
    }
    
    /**
     * Builds an instance of the appropriate Maintainable implementation for the Cash Drawer Maintainable
     * @param cashDrawerMaintenanceDocumentEntry the data dictionary entry from the Cash Drawer's maintenance document 
     * @return an appropriate Maintainable
     */
    protected Maintainable createCashDrawerMaintainable(MaintenanceDocumentEntry cashDrawerMaintenanceDocumentEntry) {
        Maintainable cashDrawerMaintainable;
        try {
            cashDrawerMaintainable = cashDrawerMaintenanceDocumentEntry.getMaintainableClass().newInstance();
        }
        catch (InstantiationException ie) {
            throw new RuntimeException("Cannot instantiate instance of maintainable implementation "+cashDrawerMaintenanceDocumentEntry.getMaintainableClass().getName(), ie);
        }
        catch (IllegalAccessException iae) {
            throw new RuntimeException("Illegal access occurred while instantiating instance of maintainable implementation "+cashDrawerMaintenanceDocumentEntry.getMaintainableClass().getName(), iae);
        }
        return cashDrawerMaintainable;
    }
}
