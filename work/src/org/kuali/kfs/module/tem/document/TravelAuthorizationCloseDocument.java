/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.kfs.module.tem.document;

import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeIndexingQueue;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;

public class TravelAuthorizationCloseDocument extends TravelAuthorizationDocument {

    @Override
    public boolean generateGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
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

            if (isTripGenerateEncumbrance()){
                getTravelEncumbranceService().disencumberTravelAuthorizationClose(this);
            }

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
}
