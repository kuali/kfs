/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.validation.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.sys.KfsAuthorizationConstants;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.impl.AccountingLineAccessibleValidation;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentAuthorizer;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentPresentationController;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Overridden to more gracefully handle advance accounting lines
 */
public class TravelAuthorizationAccountingLineAccessibleValidation extends AccountingLineAccessibleValidation {
    protected DocumentHelperService documentHelperService;

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
        if ( workflowDocument.isEnroute() && workflowDocument.isApprovalRequested() && StringUtils.equals(TemConstants.TRAVEL_ADVANCE_ACCOUNTING_LINE_TYPE_CODE, getAccountingLineForValidation().getFinancialDocumentLineTypeCode())) {
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
        TransactionalDocumentAuthorizer documentAuthorizer = (TransactionalDocumentAuthorizer) getDocumentHelperService().getDocumentAuthorizer(accountingDocument);
        TransactionalDocumentPresentationController presentationController = (TransactionalDocumentPresentationController) getDocumentHelperService().getDocumentPresentationController(accountingDocument);

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
        CANDIDATE_EDIT_MODES.add(KfsAuthorizationConstants.TransactionalEditMode.FRN_ENTRY);
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
     * Returns advance accounting line if possible
     * @see org.kuali.kfs.sys.document.validation.impl.AccountingLineAccessibleValidation#getGroupName()
     */
    @Override
    protected String getGroupName() {
        if (TemConstants.TRAVEL_ADVANCE_ACCOUNTING_LINE_TYPE_CODE.equals(getAccountingLineForValidation().getFinancialDocumentLineTypeCode())) {
            return TemConstants.TRAVEL_ADVANCE_ACCOUNTING_LINE_GROUP_NAME;
        }
        return super.getGroupName();
    }

    /**
     * Returns the advance accounting line permission if
     * @see org.kuali.kfs.sys.document.validation.impl.AccountingLineAccessibleValidation#getAccountingLineCollectionProperty()
     */
    @Override
    protected String getAccountingLineCollectionProperty() {
        String propertyName = null;
        if (GlobalVariables.getMessageMap().getErrorPath().size() > 0) {
            propertyName = GlobalVariables.getMessageMap().getErrorPath().get(0).replaceFirst(".*?document\\.", ""); // respect error path no matter what
            if (propertyName.equals("newAdvanceLine")) {
                return TemConstants.PermissionAttributeValue.ADVANCE_ACCOUNTING_LINES.value;
            }
        }
        if (TemConstants.TRAVEL_ADVANCE_ACCOUNTING_LINE_TYPE_CODE.equals(getAccountingLineForValidation().getFinancialDocumentLineTypeCode())) {
            return TemConstants.PermissionAttributeValue.ADVANCE_ACCOUNTING_LINES.value;
        }
        return super.getAccountingLineCollectionProperty();
    }

    public DocumentHelperService getDocumentHelperService() {
        return documentHelperService;
    }

    public void setDocumentHelperService(DocumentHelperService documentHelperService) {
        this.documentHelperService = documentHelperService;
    }
}
