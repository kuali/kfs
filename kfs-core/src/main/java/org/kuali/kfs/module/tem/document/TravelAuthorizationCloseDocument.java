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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeIndexingQueue;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;

public class TravelAuthorizationCloseDocument extends TravelAuthorizationDocument {
    protected String travelReimbursementDocumentNumber;

    @Override
    public boolean generateGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        return true;
    }

    @Override
    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        if (isTripGenerateEncumbrance()){
            List<GeneralLedgerPendingEntry> reimbursementPendingEntries = null;
            if (!StringUtils.isBlank(getTravelReimbursementDocumentNumber())) { // we were spawned by a TR; let's find the GLPE's for that
                Map<String, String> fieldValues = new HashMap<String, String>();
                fieldValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, getTravelReimbursementDocumentNumber());
                reimbursementPendingEntries = (List<GeneralLedgerPendingEntry>)getBusinessObjectService().findMatching(GeneralLedgerPendingEntry.class, fieldValues);
            }

            getTravelEncumbranceService().disencumberTravelAuthorizationClose(this, sequenceHelper, reimbursementPendingEntries);
        }
        return true;
    }



    /**
     * @see org.kuali.rice.kns.document.Document#doRouteStatusChange(org.kuali.rice.kew.dto.DocumentRouteStatusChange)
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {

        super.doRouteStatusChange(statusChangeEvent);

        //doc is final / processed
        if (DocumentStatus.PROCESSED.getCode().equals(statusChangeEvent.getNewRouteStatus())) {

            retirePreviousAuthorizations();

            final DocumentAttributeIndexingQueue documentAttributeIndexingQueue = KewApiServiceLocator.getDocumentAttributeIndexingQueue();
            try {
                updateAndSaveAppDocStatus(TravelAuthorizationStatusCodeKeys.CLOSED);
                documentAttributeIndexingQueue.indexDocument(getDocumentNumber());
            }
            catch (WorkflowException we) {
                throw new RuntimeException("Workflow document exception while updating related documents", we);
            }
        }
    }

    /**
     * Override to do nothing - travel auth close's don't have advances or payments associated with those
     * @see org.kuali.kfs.module.tem.document.TravelAuthorizationDocument#initiateAdvancePaymentAndLines()
     */
    @Override
    protected void initiateAdvancePaymentAndLines() {}

    /**
     * Always return true - we always need to do extra work on document copy to revert this to the original TA
     * @see org.kuali.kfs.module.tem.document.TravelAuthorizationDocument#shouldRevertToOriginalAuthorizationOnCopy()
     */
    @Override
    public boolean shouldRevertToOriginalAuthorizationOnCopy() {
        return true;
    }

    /**
     * @return the document number of the final travel reimbursement which spawned this TAC
     */
    public String getTravelReimbursementDocumentNumber() {
        return travelReimbursementDocumentNumber;
    }

    /**
     * Sets the document number of the final travel reimburement document which spawned this document
     * @param travelReimbursementDocumentNumber the document number to set
     */
    public void setTravelReimbursementDocumentNumber(String travelReimbursementDocumentNumber) {
        this.travelReimbursementDocumentNumber = travelReimbursementDocumentNumber;
    }

    @Override
    public boolean isTripProgenitor() {
        return false; // TAA's are never trip progenitors
    }

    @Override
    public void setTripProgenitor(boolean tripProgenitor) {}

    /**
     * It's pointless to mask the trip identifier on the close - it's already known
     * @see org.kuali.kfs.module.tem.document.TravelAuthorizationDocument#maskTravelDocumentIdentifierAndOrganizationDocNumber()
     */
    @Override
    public boolean maskTravelDocumentIdentifierAndOrganizationDocNumber() {
        return false;
    }
}
