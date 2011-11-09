/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.bc.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionDuration;
import org.kuali.kfs.module.bc.service.BudgetConstructionDurationService;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * implements the service methods defined in BudgetConstructionDurationService
 */
public class BudgetConstructionDurationServiceImpl implements BudgetConstructionDurationService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionDurationServiceImpl.class);

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.bc.service.BudgetConstructionDurationService#getByPrimaryId(java.lang.String)
     */
    public BudgetConstructionDuration getByPrimaryId(String durationCode) {
        Map<String, Object> primaryKeys = new HashMap<String, Object>();
        primaryKeys.put(BCPropertyConstants.APPOINTMENT_DURATION_CODE, durationCode);

        return (BudgetConstructionDuration) businessObjectService.findByPrimaryKey(BudgetConstructionDuration.class, primaryKeys);
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
