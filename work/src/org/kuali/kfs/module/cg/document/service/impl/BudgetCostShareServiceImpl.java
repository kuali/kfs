/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source$
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
package org.kuali.module.kra.budget.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.kra.budget.bo.BudgetThirdPartyCostShare;
import org.kuali.module.kra.budget.bo.BudgetInstitutionCostShare;
import org.kuali.module.kra.budget.bo.BudgetUser;
import org.kuali.module.kra.budget.bo.InstitutionCostSharePersonnel;
import org.kuali.module.kra.budget.service.BudgetCostShareService;

/**
 * 
 * This class...
 * 
 * 
 */
public class BudgetCostShareServiceImpl implements BudgetCostShareService {

    public void cleanseCostShare(boolean institutionCostShareIndicator, List<BudgetInstitutionCostShare> budgetInstitutionCostShare, boolean budgetThirdPartyCostShareIndicator, List<BudgetThirdPartyCostShare> budgetThirdPartyCostShare, List<BudgetUser> personnel, List<InstitutionCostSharePersonnel> institutionCostSharePersonnel) {
        if (!institutionCostShareIndicator) {
            // institution cost share gone, thus remove manually entered data
            budgetInstitutionCostShare.clear();
        }

        if (!budgetThirdPartyCostShareIndicator) {
            // third party cost share gone, thus remove manually entered data
            budgetThirdPartyCostShare.clear();
        }

        // Make a copy of the list so to avoid ConcurrentModificationException.
        List<InstitutionCostSharePersonnel> institutionCostSharePersonnelCopy = new ArrayList(institutionCostSharePersonnel);
        
        // Check that all the institutionCostSharePersonnel chart/orgs still have personnel associated with them, if not, remove
        // the ones that are orphans. I could use BudgetUser.contains here but for the type of comparision I need, I would need
        // a misleading BudgetUser.equals method... I'm not so sure if that's useful.
        for (InstitutionCostSharePersonnel institutionCostSharePerson : institutionCostSharePersonnelCopy) {
            boolean found = false;

            for (BudgetUser person : personnel) {
                if (institutionCostSharePerson.getChartOfAccountsCode().equals(person.getFiscalCampusCode()) && institutionCostSharePerson.getOrganizationCode().equals(person.getPrimaryDepartmentCode())) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                institutionCostSharePersonnel.remove(institutionCostSharePerson);
            }
        }
    }

    public void reconcileCostShare(String documentNumber, List<BudgetUser> personnel, List<InstitutionCostSharePersonnel> institutionCostSharePersonnel) {
        for (BudgetUser person : personnel) {
            InstitutionCostSharePersonnel institutionCostSharePerson = new InstitutionCostSharePersonnel();
            institutionCostSharePerson.setDocumentNumber(documentNumber);
            institutionCostSharePerson.setChartOfAccountsCode(StringUtils.defaultString(person.getFiscalCampusCode()));
            institutionCostSharePerson.setOrganizationCode(StringUtils.defaultString(person.getPrimaryDepartmentCode()));

            // Check if it already is in list, if it is, don't add. It will also check that chart & org are set. That is important
            // because TO BE NAMED people may not have a chart / org, we don't want to add those at this time.
            if (!ObjectUtils.collectionContainsObjectWithIdentitcalKey(institutionCostSharePersonnel, institutionCostSharePerson) && !StringUtils.isEmpty(institutionCostSharePerson.getChartOfAccountsCode()) && !StringUtils.isEmpty(institutionCostSharePerson.getOrganizationCode())) {
                institutionCostSharePersonnel.add(institutionCostSharePerson);
            }
        }
    }
}
