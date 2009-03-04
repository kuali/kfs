/*
 * Copyright 2008 The Kuali Foundation.
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
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentAuthorizer;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentPresentationController;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.util.GlobalVariables;

public class DisbursementVoucherAccountingLineTotalsValidation extends AccountingLineGroupTotalsUnchangedValidation {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherAccountingLineTotalsValidation.class);

    /**
     * @see org.kuali.kfs.sys.document.validation.impl.AccountingLineGroupTotalsUnchangedValidation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        LOG.debug("validate start");

        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) event.getDocument();
        Person financialSystemUser = GlobalVariables.getUserSession().getPerson();
        
        // amounts can only decrease
        List<String> candidateEditModes = this.getCandidateEditModes();
        if (this.hasRequiredEditMode(dvDocument, financialSystemUser, candidateEditModes)) {

            //users in foreign or wire workgroup can increase or decrease amounts because of currency conversion            
            List<String> foreignDraftAndWireTransferEditModes = this.getForeignDraftAndWireTransferEditModes();
            if (!this.hasRequiredEditMode(dvDocument, financialSystemUser, foreignDraftAndWireTransferEditModes)) {
                DisbursementVoucherDocument persistedDocument = (DisbursementVoucherDocument) retrievePersistedDocument(dvDocument);
                if (persistedDocument == null) {
                    handleNonExistentDocumentWhenApproving(dvDocument);
                    return true;
                }

                // check total cannot decrease
                if (persistedDocument.getDisbVchrCheckTotalAmount().isLessThan(dvDocument.getDisbVchrCheckTotalAmount())) {
                    GlobalVariables.getErrorMap().putError(KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.DISB_VCHR_CHECK_TOTAL_AMOUNT, KFSKeyConstants.ERROR_DV_CHECK_TOTAL_CHANGE);
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
     * @param accountingDocument the given accounting document
     * @param financialSystemUser the given user
     * @param candidateEditEditModes the given candidate edit modes
     * @return true if the give user has permission to any edit mode defined in the given candidate edit modes; otherwise, false
     */
    private boolean hasRequiredEditMode(AccountingDocument accountingDocument, Person financialSystemUser, List<String> candidateEditModes) {
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

    /**
     * define the possibly desired edit modes
     * 
     * @return the possibly desired edit modes
     */
    private List<String> getCandidateEditModes() {
        List<String> candidateEdiModes = new ArrayList<String>();
        candidateEdiModes.add(DisbursementVoucherEditMode.TAX_ENTRY);
        candidateEdiModes.add(DisbursementVoucherEditMode.FRN_ENTRY);
        candidateEdiModes.add(DisbursementVoucherEditMode.TRAVEL_ENTRY);
        candidateEdiModes.add(DisbursementVoucherEditMode.WIRE_ENTRY);

        return candidateEdiModes;
    }
    
    /**
     * get foreign draft And wire transfer edit mode names
     * @return foreign draft And wire transfer edit mode names
     */
    private List<String> getForeignDraftAndWireTransferEditModes() {
        List<String> foreignDraftAndWireTransferEditModes = new ArrayList<String>();
        foreignDraftAndWireTransferEditModes.add(DisbursementVoucherEditMode.FRN_ENTRY);
        foreignDraftAndWireTransferEditModes.add(DisbursementVoucherEditMode.WIRE_ENTRY);

        return foreignDraftAndWireTransferEditModes;
    }
}
