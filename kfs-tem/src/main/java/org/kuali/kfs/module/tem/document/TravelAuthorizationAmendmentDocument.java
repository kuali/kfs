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
