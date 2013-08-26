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
package org.kuali.kfs.fp.document.authorization;

import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.fp.businessobject.CashDrawer;
import org.kuali.kfs.fp.document.CashReceiptDocument;
import org.kuali.kfs.fp.service.CashDrawerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KfsAuthorizationConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.LedgerPostingDocumentPresentationControllerBase;
import org.kuali.kfs.sys.service.FinancialSystemWorkflowHelperService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;

public class CashReceiptDocumentPresentationController extends LedgerPostingDocumentPresentationControllerBase {
    private static final String CASH_MANAGEMENT_NODE_NAME = "CashManagement";

    /**
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#canApprove(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canApprove(Document document) {
        return this.canApproveOrBlanketApprove(document) ? super.canApprove(document) : false;
    }

    /**
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#canBlanketApprove(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canBlanketApprove(Document document) {
        // blanket approve only available for cash management confirm edit mode
        if(!this.isInCashManageConfirmMode(document)){
            return false;
        }

        return this.canApproveOrBlanketApprove(document) ? super.canBlanketApprove(document) : false;
    }

    protected boolean canApproveOrBlanketApprove(Document document) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        if (workflowDocument.isApprovalRequested() ) {
            if (!SpringContext.getBean(FinancialSystemWorkflowHelperService.class).isAdhocApprovalRequestedForPrincipal(workflowDocument, GlobalVariables.getUserSession().getPrincipalId())) {
                CashReceiptDocument cashReceiptDocument = (CashReceiptDocument) document;

                String campusCode = cashReceiptDocument.getCampusLocationCode();
                CashDrawer cashDrawer = SpringContext.getBean(CashDrawerService.class).getByCampusCode(campusCode);
                if (cashDrawer == null) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.CashReceipt.ERROR_CASH_DRAWER_DOES_NOT_EXIST, campusCode);
                    return false;
                }
                if (cashDrawer.isClosed()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Prevents editing of the document at the CashManagement node
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#canEdit(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canEdit(Document document) {
        Set<String> currentNodeNames = document.getDocumentHeader().getWorkflowDocument().getCurrentNodeNames();
        if (CollectionUtils.isNotEmpty(currentNodeNames) && currentNodeNames.contains(CashReceiptDocumentPresentationController.CASH_MANAGEMENT_NODE_NAME)) {
            return false;
        }
        return super.canEdit(document);
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase#getEditModes(org.kuali.rice.krad.document.Document)
     */
    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        addFullEntryEntryMode(document, editModes);
        addChangeRequestMode(document, editModes);

        return editModes;
    }

    protected void addFullEntryEntryMode(Document document, Set<String> editModes) {
        if (this.isInCashManageConfirmMode(document)){
            editModes.add(KfsAuthorizationConstants.CashReceiptEditMode.CASH_MANAGER_CONFIRM_MODE);
        }
    }

    protected void addChangeRequestMode(Document document, Set<String> editModes) {
        boolean IndValue = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(CashReceiptDocument.class, "CHANGE_REQUEST_ENABLED_IND");
        if(IndValue) {
            editModes.add(KfsAuthorizationConstants.CashReceiptEditMode.CHANGE_REQUEST_MODE);
        }
    }

    /**
     * determine whether the given document is in cash management confirm edit mode
     */
    protected boolean isInCashManageConfirmMode(Document document){
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        if (workflowDocument.isEnroute()) {
            Set<String> currentRouteLevels = workflowDocument.getCurrentNodeNames();
            if(CollectionUtils.isNotEmpty(currentRouteLevels) && currentRouteLevels.contains(CASH_MANAGEMENT_NODE_NAME)) {
                return true;
            }
        }

        return false;
    }
}
