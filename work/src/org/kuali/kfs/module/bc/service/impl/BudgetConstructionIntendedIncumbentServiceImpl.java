/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.bc.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbent;
import org.kuali.kfs.module.bc.service.BudgetConstructionIntendedIncumbentService;
import org.kuali.kfs.sys.KFSPropertyConstants;

/**
 * implements the service methods defined in BudgetConstructionIntendedIncumbentService
 */
public class BudgetConstructionIntendedIncumbentServiceImpl implements BudgetConstructionIntendedIncumbentService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionIntendedIncumbentServiceImpl.class);

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.bc.service.BudgetConstructionIntendedIncumbentService#getByPrimaryId(java.lang.String)
     */
    public BudgetConstructionIntendedIncumbent getByPrimaryId(String emplid) {
        Map<String, Object> primaryKeys = new HashMap<String, Object>();
        primaryKeys.put(KFSPropertyConstants.EMPLID, emplid);

        return (BudgetConstructionIntendedIncumbent) businessObjectService.findByPrimaryKey(BudgetConstructionIntendedIncumbent.class, primaryKeys);
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
