/*
 * Copyright 2007 The Kuali Foundation
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
/*
 * Created on Jul 7, 2004
 *
 */
package org.kuali.kfs.pdp.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.service.CustomerProfileService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CustomerProfileServiceImpl implements CustomerProfileService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerProfileServiceImpl.class);

    private BusinessObjectService businessObjectService;

    public CustomerProfile get(String chartCode, String unitCode, String subUnitCode) {
        Map fieldValues = new HashMap();
        
        fieldValues.put(PdpPropertyConstants.CustomerProfile.CUSTOMER_PROFILE_CHART_CODE, chartCode);
        fieldValues.put(PdpPropertyConstants.CustomerProfile.CUSTOMER_PROFILE_UNIT_CODE, unitCode);
        fieldValues.put(PdpPropertyConstants.CustomerProfile.CUSTOMER_PROFILE_SUB_UNIT_CODE, subUnitCode);
        List customerProfileList = (List) this.businessObjectService.findMatching(CustomerProfile.class, fieldValues);
        if (customerProfileList.isEmpty()) return null;
        
        return (CustomerProfile) customerProfileList.get(0);
    }
    
    /**
     * Sets the business object service
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
