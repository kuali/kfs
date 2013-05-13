/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.Tickler;
import org.kuali.kfs.module.endow.document.service.TicklerService;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This class provides implementation for Tickler related methods.
 */
public class TicklerServiceImpl implements TicklerService {

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.endow.document.service.TicklerService#getSecurityActiveTicklers(java.lang.String)
     */
    public List<Tickler> getSecurityActiveTicklers(String securityId) {
        List<Tickler> result = new ArrayList<Tickler>();
        Map<String, String> criteria = new HashMap<String, String>();

        criteria.put(EndowPropertyConstants.TICKLER_SECURITIES + "." + EndowPropertyConstants.TICKLER_SECURITYID, securityId);
        criteria.put(EndowPropertyConstants.TICKLER_ACTIVE_INDICATOR, Boolean.TRUE.toString());

        result = (List<Tickler>) businessObjectService.findMatchingOrderBy(Tickler.class, criteria, EndowPropertyConstants.TICKLER_ENTRY_DETAIL, true);

        return result;
    }

    /**
     * Gets the businessObjectService.
     * 
     * @return businessObjectService
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService.
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
