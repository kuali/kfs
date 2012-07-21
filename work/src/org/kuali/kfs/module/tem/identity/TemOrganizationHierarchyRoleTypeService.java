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

import org.kuali.kfs.module.tem.service.TravelerService;
import org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase;

public abstract class TemOrganizationHierarchyRoleTypeService extends KimRoleTypeServiceBase {

    private TravelerService travelerService;

    public static final String PERFORM_QUALIFIER_MATCH = "performQualifierMatch";
    
    public boolean isParentOrg(String qualificationChartCode, String qualificationOrgCode, String roleChartCode, String roleOrgCode, boolean descendHierarchy) {
        return getTravelerService().isParentOrg(qualificationChartCode, qualificationOrgCode, roleChartCode, roleOrgCode, descendHierarchy);
    }

    /**
     * Gets the travelerService attribute. 
     * @return Returns the travelerService.
     */
    public TravelerService getTravelerService() {
        return travelerService;
    }

    /**
     * Sets the travelerService attribute value.
     * @param travelerService The travelerService to set.
     */
    public void setTravelerService(TravelerService travelerService) {
        this.travelerService = travelerService;
    }

    

}
