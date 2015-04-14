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
package org.kuali.kfs.module.bc.document.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionProcessorService;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.springframework.transaction.annotation.Transactional;

/**
 * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionProcessorService
 */
@Transactional(readOnly=true)
public class BudgetConstructionProcessorServiceImpl implements BudgetConstructionProcessorService {
    private static final Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionProcessorServiceImpl.class);

    protected OrganizationService organizationService;

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionProcessorService#getProcessorOrgs(org.kuali.rice.kim.api.identity.Person)
     */
    public List<Organization> getProcessorOrgs(Person person) {
        List<Organization> processorOrgs = new ArrayList<Organization>();
        Map<String, String> qualification = new HashMap<String,String>();
        List<Map<String,String>> allQualifications = getRoleService().getNestedRoleQualifersForPrincipalByNamespaceAndRolename(person.getPrincipalId(), BCConstants.BUDGET_CONSTRUCTION_NAMESPACE, BCConstants.KimApiConstants.BC_PROCESSOR_ROLE_NAME, qualification);
        for (Map<String,String> attributeSet : allQualifications) {
            String chartOfAccountsCode = attributeSet.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
            String organizationCode = attributeSet.get(KfsKimAttributes.ORGANIZATION_CODE);

            if (StringUtils.isNotBlank(chartOfAccountsCode) && StringUtils.isNotBlank(organizationCode)) {
                Organization org = organizationService.getByPrimaryId(chartOfAccountsCode, organizationCode);
                if (org != null && !processorOrgs.contains(org)) {
                    processorOrgs.add(org);
                }
            }
        }

        return processorOrgs;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionProcessorService#isOrgProcessor(java.lang.String,
     *      java.lang.String, org.kuali.rice.kim.api.identity.Person)
     */
    public boolean isOrgProcessor(String chartOfAccountsCode, String organizationCode, Person person) {
        Map<String,String> qualification = new HashMap<String,String>();
        qualification.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        qualification.put(KfsKimAttributes.ORGANIZATION_CODE, organizationCode);

        return getRoleService().principalHasRole(person.getPrincipalId(), getBudgetProcessorRoleIds(), qualification);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionProcessorService#isOrgProcessor(org.kuali.kfs.coa.businessobject.Organization,
     *      org.kuali.rice.kim.api.identity.Person)
     */
    public boolean isOrgProcessor(Organization organization, Person person) {
        try {
            return isOrgProcessor(organization.getChartOfAccountsCode(), organization.getOrganizationCode(), person);
        }
        catch (Exception e) {
            String errorMessage = String.format("Fail to determine if %s is an approver for %s. ", person, organization);
            LOG.info(errorMessage, e);
        }

        return false;
    }
    
    /**
     * @return role id for the budget processor role
     */
    protected List<String> getBudgetProcessorRoleIds() {
        return Collections.singletonList( getRoleService().getRoleIdByNamespaceCodeAndName(BCConstants.BUDGET_CONSTRUCTION_NAMESPACE, BCConstants.KimApiConstants.BC_PROCESSOR_ROLE_NAME));
    }

    protected RoleService getRoleService() {
        return KimApiServiceLocator.getRoleService();
    }

    public void setOrganizationService(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

}
