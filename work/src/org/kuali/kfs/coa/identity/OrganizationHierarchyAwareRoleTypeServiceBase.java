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
package org.kuali.kfs.coa.identity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.identity.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase;

public abstract class OrganizationHierarchyAwareRoleTypeServiceBase extends KimRoleTypeServiceBase {
    
    private OrganizationService organizationService;

    protected final String DESCEND_HIERARCHY_TRUE_VALUE = "Y";
    
    protected boolean doDescendHierarchy(String descendHierarchyValue){
        return DESCEND_HIERARCHY_TRUE_VALUE.equalsIgnoreCase(descendHierarchyValue);
    }
    
    protected boolean isParentOrg(String qualificationChartCode, String qualificationOrgCode, String roleChartCode, String roleOrgCode, 
            boolean descendHierarchy) {
        
        // perform exact match first
        if(StringUtils.equals(qualificationChartCode, roleChartCode)
                && StringUtils.equals(qualificationOrgCode, roleOrgCode) ) {
            return true;
        } else {
            // if descendHierarchy is false, abort now
            if(!descendHierarchy){
                return false;
            }
            // otherwise, check if the organization/chart in the role qualifier implies
            // the organization in the qualification
            
            // first, just check on a chart match - if there is no organization on the role qualifier
            if(StringUtils.isBlank(roleOrgCode)){
                if(StringUtils.equals(qualificationChartCode, roleChartCode)){
                    return true;
                } else {
                    // TODO: need to check on the chart hierarchy?
                    return false;
                }
            }
            
            // now that we know that we have a chart and an org on the role qualifier,
            // test to see if they match
            return organizationService.isParentOrg(qualificationChartCode, qualificationOrgCode, roleChartCode, roleOrgCode);
        }
        
    }

    /**
     * Gets the organizationService attribute. 
     * @return Returns the organizationService.
     */
    public OrganizationService getOrganizationService() {
        return organizationService;
    }

    /**
     * Sets the organizationService attribute value.
     * @param organizationService The organizationService to set.
     */
    public void setOrganizationService(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

}