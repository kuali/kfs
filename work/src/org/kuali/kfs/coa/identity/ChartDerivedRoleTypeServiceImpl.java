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
