/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.kra.routingform.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.Constants;
import org.kuali.PropertyConstants;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.module.kra.routingform.bo.Purpose;
import org.kuali.module.kra.routingform.service.PurposeService;

public class PurposeServiceImpl implements PurposeService {

    private BusinessObjectService businessObjectService;
    
    /**
     * @see org.kuali.module.kra.routingform.service.PurposeService#getPurposes()
     */
    public List<Purpose> getPurposes() {
        Map fieldValues = new HashMap();
        fieldValues.put(PropertyConstants.DATA_OBJECT_MAINTENANCE_CODE_ACTIVE_INDICATOR, Constants.ACTIVE_INDICATOR);
        
        Collection col = businessObjectService.findMatchingOrderBy(Purpose.class, fieldValues, PropertyConstants.USER_SORT_NUMBER, true);
        
        return new ArrayList(col);
    }
    
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
