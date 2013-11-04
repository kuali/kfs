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
import java.util.List;
import java.util.Set;

import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.KfsAuthorizationConstants.DisbursementVoucherEditMode;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.impl.AccountingLineGroupTotalsUnchangedValidation;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentAuthorizer;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentPresentationController;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.krad.util.GlobalVariables;

public class DisbursementVoucherAccountingLineTotalsValidation extends AccountingLineGroupTotalsUnchangedValidation {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherAccountingLineTotalsValidation.class);

    /**
     * @see org.kuali.kfs.sys.document.validation.impl.AccountingLineGroupTotalsUnchangedValidation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("validate start");
        }

        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) event.getDocument();


        Person financialSystemUser = GlobalVariables.getUserSession().getPerson();
        final Set<String> currentEditModes = getEditModesFromDocument(dvDocument, financialSystemUser);

        // amounts can only decrease
        List<String> candidateEditModes = this.getCandidateEditModes();
        if (this.hasRequiredEditMode(currentEditModes, candidateEditModes)) {

            //users in foreign or wire workgroup can increase or decrease amounts because of currency conversion
            List<String> foreignDraftAndWireTransferEditModes = this.getForeignDraftAndWireTransferEditModes(dvDocument);
            if (!this.hasRequiredEditMode(currentEditModes, foreignDraftAndWireTransferEditModes)) {
                DisbursementVoucherDocument persistedDocument = (DisbursementVoucherDocument) retrievePersistedDocument(dvDocument);
                if (persistedDocument == null) {
                    handleNonExistentDocumentWhenApproving(dvDocument);
                    return true;
                }
                // KFSMI- 5183
                if (persistedDocument.getDocumentHeader().getWorkflowDocument().isSaved() && persistedDocument.getDisbVchrCheckTotalAmount().isZero()) {
                    return true;
                }

                // check total cannot decrease
                if (!persistedDocument.getDocumentHeader().getWorkflowDocument().isCompletionRequested() && persistedDocument.getDisbVchrCheckTotalAmount().isLessThan(dvDocument.getDisbVchrCheckTotalAmount())) {
                    GlobalVariables.getMessageMap().putError(KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.DISB_VCHR_CHECK_TOTAL_AMOUNT, KFSKeyConstants.ERROR_DV_CHECK_TOTAL_CHANGE);
                    return false;
                }
            }

            return true;
        }

        return super.validate(event);
    }

    /**
     * determine whether the give user has permission to any edit mode defined in the given candidate edit modes
     *
     * @param currentEditModes the edit modes currently available to the given user on the document
     * @param candidateEditEditModes the given candidate edit modes
     * @return true if the give user has permission to any edit mode defined in the given candidate edit modes; otherwise, false
     */
    protected boolean hasRequiredEditMode(Set<String> currentEditModes, List<String> candidateEditModes) {
        for (String editMode : candidateEditModes) {
            if (currentEditModes.contains(editMode)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Retrieves the current edit modes from the document
     * @param accountingDocument the document to find edit modes of
     * @param financialSystemUser the user requesting the edit modes
     * @return the Set of current edit modes
     */
    protected Set<String> getEditModesFromDocument(AccountingDocument accountingDocument, Person financialSystemUser) {
        final DocumentHelperService documentHelperService = SpringContext.getBean(DocumentHelperService.class);
        final TransactionalDocumentAuthorizer documentAuthorizer = (TransactionalDocumentAuthorizer) documentHelperService.getDocumentAuthorizer(accountingDocument);
        final TransactionalDocumentPresentationController presentationController = (TransactionalDocumentPresentationController) documentHelperService.getDocumentPresentationController(accountingDocument);

        final Set<String> presentationControllerEditModes = presentationController.getEditModes(accountingDocument);
        final Set<String> editModes = documentAuthorizer.getEditModes(accountingDocument, financialSystemUser, presentationControllerEditModes);

        return editModes;
    }

    /**
     * define the possibly desired edit modes
     *
     * @return the possibly desired edit modes
     */
    protected List<String> getCandidateEditModes() {
        List<String> candidateEdiModes = new ArrayList<String>();
        candidateEdiModes.add(DisbursementVoucherEditMode.TAX_ENTRY);
        candidateEdiModes.add(DisbursementVoucherEditMode.FRN_ENTRY);
        candidateEdiModes.add(DisbursementVoucherEditMode.TRAVEL_ENTRY);
        candidateEdiModes.add(DisbursementVoucherEditMode.WIRE_ENTRY);

        return candidateEdiModes;
    }

    /**
     * get foreign draft And wire transfer edit mode names, as well as tax if the payee is a non-resident alien
     * @param dvDocument the document we're validating
     * @return foreign draft And wire transfer edit mode names
     */
    protected List<String> getForeignDraftAndWireTransferEditModes(DisbursementVoucherDocument dvDocument) {
        List<String> foreignDraftAndWireTransferEditModes = new ArrayList<String>();
        foreignDraftAndWireTransferEditModes.add(DisbursementVoucherEditMode.FRN_ENTRY);
        foreignDraftAndWireTransferEditModes.add(DisbursementVoucherEditMode.WIRE_ENTRY);

        if (includeTaxAsTotalChangingMode(dvDocument)) {
            foreignDraftAndWireTransferEditModes.add(DisbursementVoucherEditMode.TAX_ENTRY);
        }

        return foreignDraftAndWireTransferEditModes;
    }

    /**
     * Determines whether the tax edit mode should be allowed to change the accounting line totals,
     * based on whether the payee is a non-resident alient or not
     * @param dvDocument the document to check
     * @return true if the tax entry mode can change accounting line totals, false otherwise
     */
    protected boolean includeTaxAsTotalChangingMode(DisbursementVoucherDocument dvDocument) {
        return dvDocument.getDvPayeeDetail().isDisbVchrAlienPaymentCode();
    }
}
