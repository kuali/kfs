/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.coa.service.impl;

import org.kuali.kfs.coa.businessobject.ResponsibilityCenter;
import org.kuali.kfs.coa.service.ResponsibilityCenterService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.krad.service.BusinessObjectService;

@NonTransactional
public class ResponsibilityCenterServiceImpl implements ResponsibilityCenterService{

    private BusinessObjectService businessObjectService;

    /**
     * This method retrieves a responsibility instance by its primary key (parameters passed in).
     *
     * @param responsibilityCenterCode
     * @return A ResponsibilityCenter instance.
     */
    @Override
    public ResponsibilityCenter getByPrimaryId(String responsibilityCenterCode) {
        return businessObjectService.findBySinglePrimaryKey(ResponsibilityCenter.class, responsibilityCenterCode);
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
