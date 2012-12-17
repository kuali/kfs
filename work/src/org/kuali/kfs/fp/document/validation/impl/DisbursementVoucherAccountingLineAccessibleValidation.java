/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.fp.document.validation.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.sys.KfsAuthorizationConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.impl.AccountingLineAccessibleValidation;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentAuthorizer;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentPresentationController;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.krad.util.GlobalVariables;

public class DisbursementVoucherAccountingLineAccessibleValidation extends AccountingLineAccessibleValidation {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherAccountingLineAccessibleValidation.class);

    @Deprecated
    protected AccountingLine oldAccountingLineForValidation;

    /**
     * Validates that the given accounting line is accessible for editing by the current user. <strong>This method expects a
     * document as the first parameter and an accounting line as the second</strong>
     *
     * @see org.kuali.kfs.sys.document.validation.impl.AccountingLineAccessibleValidation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        AccountingDocument accountingDocument = getAccountingDocumentForValidation();
        WorkflowDocument workflowDocument = accountingDocument.getDocumentHeader().getWorkflowDocument();
        // in certain cases - when the user has special edit modes restricted to the central office, we need to make it *possible* to edit
        // the accounting lines.
        // This only happens during routing and for approval requests
        if ( workflowDocument.isEnroute() && workflowDocument.isApprovalRequested() ) {
            if ( hasRequiredEditMode( accountingDocument, GlobalVariables.getUserSession().getPerson(), getCandidateEditModes() )) {
                return true;
            }

        }
        return super.validate(event);
    }

    /**
     * determine whether the give user has permission to any edit mode defined in the given candidate edit modes
     *
     * @param accountingDocument the given accounting document
     * @param financialSystemUser the given user
     * @param candidateEditEditModes the given candidate edit modes
     * @return true if the give user has permission to any edit mode defined in the given candidate edit modes; otherwise, false
     */
    protected boolean hasRequiredEditMode(AccountingDocument accountingDocument, Person financialSystemUser, List<String> candidateEditModes) {
        DocumentHelperService documentHelperService = SpringContext.getBean(DocumentHelperService.class);
        TransactionalDocumentAuthorizer documentAuthorizer = (TransactionalDocumentAuthorizer) documentHelperService.getDocumentAuthorizer(accountingDocument);
        TransactionalDocumentPresentationController presentationController = (TransactionalDocumentPresentationController) documentHelperService.getDocumentPresentationController(accountingDocument);

        Set<String> presentationControllerEditModes = presentationController.getEditModes(accountingDocument);
        Set<String> editModes = documentAuthorizer.getEditModes(accountingDocument, financialSystemUser, presentationControllerEditModes);

        for (String editMode : candidateEditModes) {
            if (editModes.contains(editMode)) {
                return true;
            }
        }

        return false;
    }

    protected static final List<String> CANDIDATE_EDIT_MODES = new ArrayList<String>(4);
    static {
        CANDIDATE_EDIT_MODES.add(KfsAuthorizationConstants.DisbursementVoucherEditMode.TAX_ENTRY);
        CANDIDATE_EDIT_MODES.add(KfsAuthorizationConstants.DisbursementVoucherEditMode.FRN_ENTRY);
        CANDIDATE_EDIT_MODES.add(KfsAuthorizationConstants.DisbursementVoucherEditMode.TRAVEL_ENTRY);
        CANDIDATE_EDIT_MODES.add(KfsAuthorizationConstants.DisbursementVoucherEditMode.WIRE_ENTRY);
    }

    /**
     * define the possibly desired edit modes
     *
     * @return the possibly desired edit modes
     */
    protected List<String> getCandidateEditModes() {
        return Collections.unmodifiableList(CANDIDATE_EDIT_MODES);
    }

//    /**
//     * Determines if the two given accounting lines have not have their account changed
//     * @param pollux the first accounting line to check
//     * @param castor the second accounting line to check
//     * @return true if the account is the same for the two, false otherwise
//     */
//    protected boolean accountUnchanged(AccountingLine pollux, AccountingLine castor) {
//        return StringUtils.equals(pollux.getChartOfAccountsCode(), castor.getChartOfAccountsCode())
//                && StringUtils.equals(pollux.getAccountNumber(), castor.getAccountNumber());
//    }

    /**
     * Sets the oldAccountingLineForValidation attribute value.
     * @param oldAccountingLineForValidation The oldAccountingLineForValidation to set.
     */
    @Deprecated
    public void setOldAccountingLineForValidation(AccountingLine oldAccountingLineForValidation) {
        this.oldAccountingLineForValidation = oldAccountingLineForValidation;
    }
}
