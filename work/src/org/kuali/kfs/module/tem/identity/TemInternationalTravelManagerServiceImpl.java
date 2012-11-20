/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.identity;

import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.KNSPropertyConstants;

public class TemInternationalTravelManagerServiceImpl extends KimRoleTypeServiceBase {

    /**
     * @see org.kuali.rice.kim.service.support.impl.KimTypeServiceBase#performMatch(org.kuali.rice.kim.bo.types.dto.AttributeSet, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    protected boolean performMatch(AttributeSet inputAttributeSet, AttributeSet storedAttributeSet) {
        // TODO Auto-generated method stub
        try {
            TravelDocument document = (TravelDocument) ((DocumentService)SpringContext.getBean(DocumentService.class)).getByDocumentHeaderId(inputAttributeSet.get(KNSPropertyConstants.DOCUMENT_NUMBER).toString());
            String travelerType = storedAttributeSet.get(TemPropertyConstants.TRVL_DOC_TRAVELER_TYP_CD);
            if (travelerType.equals(document.getTraveler().getTravelerTypeCode())){
                return true;
            }
                
        }
        catch (WorkflowException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
        return false;
    }
}
