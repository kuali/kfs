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
