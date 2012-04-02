/*
 * Copyright 2009 The Kuali Foundation
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
