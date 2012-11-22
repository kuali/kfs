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

import java.util.Map;

import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.kim.role.RoleTypeServiceBase;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.KRADPropertyConstants;

@SuppressWarnings("deprecation")
public class TemInternationalTravelManagerServiceImpl extends RoleTypeServiceBase {

    /**
     * @see org.kuali.rice.kns.kim.type.DataDictionaryTypeServiceBase#performMatch(java.util.Map, java.util.Map)
     */
    @Override
    protected boolean performMatch(Map<String, String> inputAttributes, Map<String, String> storedAttributes) {
        try {
            TravelDocument document = (TravelDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(inputAttributes.get(KRADPropertyConstants.DOCUMENT_NUMBER).toString());
            String travelerType = storedAttributes.get(TemPropertyConstants.TRVL_DOC_TRAVELER_TYP_CD);
            if (travelerType.equals(document.getTraveler().getTravelerTypeCode())){
                return true;
            }

        }
        catch (WorkflowException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
