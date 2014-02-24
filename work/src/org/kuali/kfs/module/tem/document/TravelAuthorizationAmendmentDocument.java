/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;


@Entity
@Table(name = "TEM_TRVL_AUTH_AMEND_DOC_T")
public class TravelAuthorizationAmendmentDocument extends TravelAuthorizationDocument {

    @Override
    public void initiateDocument() {
        super.initiateDocument();
        this.tripProgenitor = false;
    }

    /**
     * @see org.kuali.rice.kns.document.Document#doRouteStatusChange(org.kuali.rice.kew.dto.DocumentRouteStatusChange)
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {

        super.doRouteStatusChange(statusChangeEvent);

        //doc is processed
        if (DocumentStatus.PROCESSED.getCode().equals(statusChangeEvent.getNewRouteStatus())) {
            retirePreviousAuthorizations();
        }
    }

    /**
     * Sets the bank code for a new document - but it uses the TA as the doc type
     */
    @Override
    public void setDefaultBankCode() {
        Bank defaultBank = SpringContext.getBean(BankService.class).getDefaultBankByDocType(TravelAuthorizationDocument.class);
        if (defaultBank != null) {
            setFinancialDocumentBankCode(defaultBank.getBankCode());
            setBank(defaultBank);
        }
    }

    /**
     * Always return true - we always need to do extra work on document copy to revert this to the original TA
     * @see org.kuali.kfs.module.tem.document.TravelAuthorizationDocument#shouldRevertToOriginalAuthorizationOnCopy()
     */
    @Override
    public boolean shouldRevertToOriginalAuthorizationOnCopy() {
        return true;
    }

    /**
     * Overridden to adjust the encumbrance for this amendment
     * @see org.kuali.kfs.module.tem.document.TravelAuthorizationDocument#generateDocumentGeneralLedgerPendingEntries(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean result = super.generateDocumentGeneralLedgerPendingEntries(sequenceHelper);
        getTravelEncumbranceService().adjustEncumbranceForAmendment(this, sequenceHelper);
        return result;
    }

    @Override
    public boolean isTripProgenitor() {
        return false; // TAA's are never trip progenitors
    }

    @Override
    public void setTripProgenitor(boolean tripProgenitor) {}

    /**
     * It's pointless to mask the trip identifier on the amendment - it's already known
     * @see org.kuali.kfs.module.tem.document.TravelAuthorizationDocument#maskTravelDocumentIdentifierAndOrganizationDocNumber()
     */
    @Override
    public boolean maskTravelDocumentIdentifierAndOrganizationDocNumber() {
        return false;
    }
}
