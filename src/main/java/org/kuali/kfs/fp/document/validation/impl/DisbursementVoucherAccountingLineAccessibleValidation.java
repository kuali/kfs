/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
        CANDIDATE_EDIT_MODES.add(KfsAuthorizationConstants.TransactionalEditMode.FRN_ENTRY);
        CANDIDATE_EDIT_MODES.add(KfsAuthorizationConstants.DisbursementVoucherEditMode.TRAVEL_ENTRY);
        CANDIDATE_EDIT_MODES.add(KfsAuthorizationConstants.TransactionalEditMode.WIRE_ENTRY);
    }

    /**
     * define the possibly desired edit modes
     *
     * @return the possibly desired edit modes
     */
    protected List<String> getCandidateEditModes() {
        return Collections.unmodifiableList(CANDIDATE_EDIT_MODES);
    }

    /**
     * Sets the oldAccountingLineForValidation attribute value.
     * @param oldAccountingLineForValidation The oldAccountingLineForValidation to set.
     */
    @Deprecated
    public void setOldAccountingLineForValidation(AccountingLine oldAccountingLineForValidation) {
        this.oldAccountingLineForValidation = oldAccountingLineForValidation;
    }
}
