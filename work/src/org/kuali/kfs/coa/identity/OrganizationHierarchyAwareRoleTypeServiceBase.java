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

import org.kuali.kfs.coa.service.ChartHierarchyService;
import org.kuali.kfs.coa.service.OrganizationHierarchyService;
import org.kuali.kfs.sys.identity.KimAttributes;
import org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase;

public abstract class OrganizationHierarchyAwareRoleTypeServiceBase extends KimRoleTypeServiceBase {
    protected List<String> roleQualifierRequiredAttributes = new ArrayList<String>();
    protected List<String> qualificationRequiredAttributes = new ArrayList<String>();
    {
        roleQualifierRequiredAttributes.add(KimAttributes.CHART_OF_ACCOUNTS_CODE);

        qualificationRequiredAttributes.add(KimAttributes.CHART_OF_ACCOUNTS_CODE);
        qualificationRequiredAttributes.add(KimAttributes.ORGANIZATION_CODE);
    }

    private ChartHierarchyService chartService;
    private OrganizationHierarchyService organizationService;

    protected boolean isParentOrg(String qualificationChartCode, String qualificationOrgCode, String roleChartCode, String roleOrgCode, boolean descendHierarchy) {
        if (roleOrgCode == null) {
            return roleChartCode.equals(qualificationChartCode) || (descendHierarchy && chartService.isParentChart(qualificationChartCode, roleChartCode));
        }
        return (roleChartCode.equals(qualificationChartCode) && roleOrgCode.equals(qualificationOrgCode)) || (descendHierarchy && organizationService.isParentOrganization(qualificationChartCode, qualificationOrgCode, roleChartCode, roleOrgCode));
    }

    public void setOrganizationService(OrganizationHierarchyService organizationService) {
        this.organizationService = organizationService;
    }

    public void setChartService(ChartHierarchyService chartService) {
        this.chartService = chartService;
    }
}