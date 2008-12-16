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

public class OrganizationOptionalHierarchyRoleTypeServiceImpl extends KimRoleTypeServiceBase {
    
    public OrganizationService organizationService;

    protected String DESCEND_HIERARCHY_TRUE_VALUE = "Y";
    protected List<String> roleQualifierRequiredAttributes = new ArrayList<String>();
    protected List<String> qualificationRequiredAttributes = new ArrayList<String>();
    {
        roleQualifierRequiredAttributes.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        roleQualifierRequiredAttributes.add(KimAttributes.DESCEND_HIERARCHY);

        qualificationRequiredAttributes.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        qualificationRequiredAttributes.add(KFSPropertyConstants.ORGANIZATION_CODE);        
        qualificationRequiredAttributes.add(KimAttributes.DESCEND_HIERARCHY);
    }
    
    protected boolean doDescendHierarchy(String descendHierarchyValue){
        return DESCEND_HIERARCHY_TRUE_VALUE.equalsIgnoreCase(descendHierarchyValue);
    }
    
    public boolean performMatch(AttributeSet qualification, AttributeSet roleQualifier) {
        //validateRequiredAttributesAgainstReceived(roleQualifierRequiredAttributes, roleQualifier);
        validateRequiredAttributesAgainstReceived(
                qualificationRequiredAttributes, qualification, QUALIFICATION_RECEIVED_ATTIBUTES_NAME);
        validateRequiredAttributesAgainstReceived(
                roleQualifierRequiredAttributes, roleQualifier, ROLE_QUALIFIERS_RECEIVED_ATTIBUTES_NAME);
        
        String chart = qualification.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        String orgCode = qualification.get(KFSPropertyConstants.ORGANIZATION_CODE);
        String roleChart = roleQualifier.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        String roleOrgCode = roleQualifier.get(KFSPropertyConstants.ORGANIZATION_CODE);
        
        // perform exact match first
        if(StringUtils.equals(chart, roleChart)
                && StringUtils.equals(orgCode, roleOrgCode) ) {
            return true;
        } else {
            // if the exact match fails and the role or the qualification say not to traverse the
            // hierarchy, abort now
            if ( !doDescendHierarchy( roleQualifier.get(KimAttributes.DESCEND_HIERARCHY) )
                    || !doDescendHierarchy( qualification.get(KimAttributes.DESCEND_HIERARCHY) ) ) {
                return false;
            }
            // otherwise, check if the organization/chart in the role qualifier implies
            // the organization in the qualification
            
            // first, just check on a chart match - if there is no organization on the role qualifier
            if ( StringUtils.isBlank( roleOrgCode ) ) {
                if ( StringUtils.equals(chart, roleChart ) ) {
                    return true;
                } else {
                    // TODO: need to check on the chart hierarchy?
                    return false;
                }
            }
            
            // now that we know that we have a chart and an org on the role qualifier,
            // test to see if they match
            return organizationService.isParentOrg( chart, orgCode, roleChart, roleOrgCode );
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