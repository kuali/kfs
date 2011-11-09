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
package org.kuali.kfs.coa.identity;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kns.kim.role.DerivedRoleTypeServiceBase;

public class ChartDerivedRoleTypeServiceImpl extends DerivedRoleTypeServiceBase {

    private OrganizationService organizationService;

    /**
     * This service takes the following attributes:
     *     Chart Code
     * Requirements:
     *     - KFS-SYS University Chart Manger: 
     *     KFS-SYS Chart Manager where for the chart that matches the one on CA_ORG_T where CA_ORG_T.ORG_TYP_CD = U
     * 
     * @see org.kuali.rice.kns.kim.role.RoleTypeServiceBase#getPrincipalIdsFromApplicationRole(java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    public Map<String,String> convertQualificationForMemberRoles( String namespaceCode, String roleName, String memberRoleNamespaceCode, String memberRoleName, Map<String,String> qualification){
        String[] chartOrg = getOrganizationService().getRootOrganizationCode();
        if(chartOrg != null){
            String rootChartOfAccountCode = chartOrg[0];
            // copy all the other qualification attributes
            Map<String,String> processingChartQualification = new HashMap<String,String>( qualification );
            // now, override the chart
            processingChartQualification.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, rootChartOfAccountCode);
            return processingChartQualification;
        } else {
            return qualification;
        }
    }

    @Override
    protected boolean performMatch(Map<String,String> inputAttributeSet, Map<String,String> storedAttributeSet) {
        // don't perform any matching - really just a pass-thru role for the embedded KFS-SYS User role
        return true;
    }

    /**
     * Gets the organizationService attribute. 
     * @return Returns the organizationService.
     */
    protected OrganizationService getOrganizationService() {
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
