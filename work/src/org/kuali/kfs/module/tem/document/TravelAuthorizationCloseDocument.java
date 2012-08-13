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

import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO;
import org.kuali.rice.kew.util.KEWConstants;

public class TravelAuthorizationCloseDocument extends TravelAuthorizationDocument {
    
    @Override
    public boolean generateGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        return true;
    }
    
    /**
     * @see org.kuali.rice.kns.document.Document#doRouteStatusChange(org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO)
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChangeDTO statusChangeEvent) {
        
        super.doRouteStatusChange(statusChangeEvent);
      
        //doc is final / processed
        if (KEWConstants.ROUTE_HEADER_PROCESSED_CD.equals(statusChangeEvent.getNewRouteStatus())) {
            
            if (isTripGenerateEncumbrance()){
                getTravelEncumbranceService().disencumberTravelAuthorizationClose(this);
            }
        }
    }
}
