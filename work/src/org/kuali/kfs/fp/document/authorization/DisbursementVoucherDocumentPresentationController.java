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

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.sys.KfsAuthorizationConstants;
import org.kuali.kfs.sys.KFSConstants.RouteLevelNames;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentPresentationControllerBase;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

public class DisbursementVoucherDocumentPresentationController extends AccountingDocumentPresentationControllerBase {

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canBlanketApprove(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean canBlanketApprove(Document document) {
        return false;
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase#getEditModes(org.kuali.rice.kns.document.Document)
     */
    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);

        editModes.add(KfsAuthorizationConstants.DisbursementVoucherEditMode.TAX_ENTRY);
        editModes.add(KfsAuthorizationConstants.DisbursementVoucherEditMode.FRN_ENTRY);

        editModes.add(KfsAuthorizationConstants.DisbursementVoucherEditMode.TRAVEL_ENTRY);
        editModes.add(KfsAuthorizationConstants.DisbursementVoucherEditMode.WIRE_ENTRY);

        editModes.add(KfsAuthorizationConstants.DisbursementVoucherEditMode.ADMIN_ENTRY);
        editModes.add(KfsAuthorizationConstants.DisbursementVoucherEditMode.EXPENSE_SPECIAL_ENTRY);

        this.addPayeeEditEntry(document, editModes);

        return editModes;
    }

    private void addPayeeEditEntry(Document document, Set<String> editModes) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        if ((workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved())) {
            editModes.add(KfsAuthorizationConstants.DisbursementVoucherEditMode.PAYEE_EDIT_MODE);
        }
        else if (workflowDocument.stateIsEnroute()) {
            if (editModes.contains(KfsAuthorizationConstants.TransactionalEditMode.EXPENSE_ENTRY)) {
                editModes.add(KfsAuthorizationConstants.DisbursementVoucherEditMode.PAYEE_EDIT_MODE);
            }
            else {
                List<String> currentRouteLevels = getCurrentRouteLevels(workflowDocument);
                if (currentRouteLevels.contains(RouteLevelNames.ORG_REVIEW)) {
                    editModes.add(KfsAuthorizationConstants.DisbursementVoucherEditMode.PAYEE_EDIT_MODE);
                }
            }
        }

    }
}
