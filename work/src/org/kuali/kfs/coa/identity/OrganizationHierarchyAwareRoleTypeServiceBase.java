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

import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase;

import uk.ltd.getahead.dwr.util.Logger;

public abstract class OrganizationHierarchyAwareRoleTypeServiceBase extends KimRoleTypeServiceBase {
    private static final Logger LOG = Logger.getLogger(OrganizationHierarchyAwareRoleTypeServiceBase.class);
    private ChartService chartService;
    private OrganizationService organizationService;

    protected boolean isParentOrg(String qualificationChartCode, String qualificationOrgCode, String roleChartCode, String roleOrgCode, boolean descendHierarchy) {
        if (roleOrgCode == null) {
            return roleChartCode.equals(qualificationChartCode) || (descendHierarchy && chartService.isParentChart(qualificationChartCode, roleChartCode));
        }
        return (roleChartCode.equals(qualificationChartCode) && roleOrgCode.equals(qualificationOrgCode)) || (descendHierarchy && organizationService.isParentOrganization(qualificationChartCode, qualificationOrgCode, roleChartCode, roleOrgCode));
    }
    
    @Override
    public AttributeSet convertQualificationForMemberRoles(String namespaceCode, String roleName, String memberRoleNamespaceCode, String memberRoleName, AttributeSet qualification) {
        AttributeSet newQualification = new AttributeSet(qualification);
        try {
            newQualification.put(KfsKimAttributes.CAMPUS_CODE, organizationService.getByPrimaryId(qualification.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE), qualification.get(KfsKimAttributes.ORGANIZATION_CODE)).getOrganizationPhysicalCampusCode());
        }
        catch (Exception e) {
            if (LOG.isDebugEnabled()) LOG.warn("Unable to convert organization qualification to physical campus", e);
        }
        return newQualification;
    }

    public void setOrganizationService(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    public void setChartService(ChartService chartService) {
        this.chartService = chartService;
    }
}