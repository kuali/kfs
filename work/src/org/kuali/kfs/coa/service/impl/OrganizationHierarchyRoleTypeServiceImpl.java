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
package org.kuali.kfs.coa.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Org;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase;

public class OrganizationHierarchyRoleTypeServiceImpl extends KimRoleTypeServiceBase {
    
    private OrganizationService organizationService;
    
    public boolean doesRoleQualifierMatchQualification(AttributeSet qualification, AttributeSet roleQualifier) {
        if (!qualification.containsKey(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE)) {
            throw new IllegalArgumentException("chartOfAccountsCode was null or blank.");
        }
    
        if (!qualification.containsKey(KFSPropertyConstants.ORGANIZATION_CODE)) {
            throw new IllegalArgumentException("organizationCode was null or blank.");
        }
    
        if(!roleQualifier.containsKey("descendsOrgHierarchy")){
            throw new IllegalArgumentException("descendsOrgHierarchy was null or blank.");
        }
    
        if(!roleQualifier.containsKey(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE)){
            throw new IllegalArgumentException("roleQualifier chartOfAccountCode was null or blank.");  
        }
    
        if(!roleQualifier.containsKey(KFSPropertyConstants.ORGANIZATION_CODE)){
            throw new IllegalArgumentException("roleQualifier organizationCode was null or blank.");          
        }
        
        if(StringUtils.equals(qualification.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE), roleQualifier.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE))
        && StringUtils.equals(qualification.get(KFSPropertyConstants.ORGANIZATION_CODE), roleQualifier.get(KFSPropertyConstants.ORGANIZATION_CODE))){
            return true;
        }
        
        if( StringUtils.equalsIgnoreCase(roleQualifier.get("descendsOrgHierarchy"), "TRUE")){
            Org org = getOrganizationService().getByPrimaryId(qualification.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE), qualification.get(KFSPropertyConstants.ORGANIZATION_CODE));
            return hasMatchOrganization(org, roleQualifier); 
           
        }else{
            return false;
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
    
    private boolean hasMatchOrganization(Org org, AttributeSet roleQualifier){
        if(org != null && !StringUtils.isNotBlank(org.getReportsToOrganizationCode()) && !StringUtils.isNotBlank(org.getReportsToChartOfAccountsCode())) {
            String rChart = org.getReportsToChartOfAccountsCode();
            String rOrg = org.getReportsToOrganizationCode();

            if(StringUtils.equals(rChart, roleQualifier.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE))
            && StringUtils.equals(rOrg, roleQualifier.get(KFSPropertyConstants.ORGANIZATION_CODE))){
                return true;
            }
            else{
                org = getOrganizationService().getByPrimaryId(rChart, rOrg);
                hasMatchOrganization(org, roleQualifier);
                
            }   
        }
        return false;
    }

}
