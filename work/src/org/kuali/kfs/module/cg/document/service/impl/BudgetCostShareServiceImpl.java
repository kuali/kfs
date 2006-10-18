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
import org.kuali.module.kra.budget.bo.BudgetUniversityCostShare;
import org.kuali.module.kra.budget.bo.BudgetUser;
import org.kuali.module.kra.budget.bo.UniversityCostSharePersonnel;
import org.kuali.module.kra.budget.service.BudgetCostShareService;

/**
 * 
 * This class...
 * 
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class BudgetCostShareServiceImpl implements BudgetCostShareService {

    public void cleanseCostShare(boolean universityCostShareIndicator, List<BudgetUniversityCostShare> budgetUniversityCostShare, boolean budgetThirdPartyCostShareIndicator, List<BudgetThirdPartyCostShare> budgetThirdPartyCostShare, List<BudgetUser> personnel, List<UniversityCostSharePersonnel> universityCostSharePersonnel) {
        if (!universityCostShareIndicator) {
            // university cost share gone, thus remove manually entered data
            budgetUniversityCostShare.clear();
        }

        if (!budgetThirdPartyCostShareIndicator) {
            // third party cost share gone, thus remove manually entered data
            budgetThirdPartyCostShare.clear();
        }

        // Make a copy of the list so to avoid ConcurrentModificationException.
        List<UniversityCostSharePersonnel> universityCostSharePersonnelCopy = new ArrayList(universityCostSharePersonnel);
        
        // Check that all the universityCostSharePersonnel chart/orgs still have personnel associated with them, if not, remove
        // the ones that are orphans. I could use BudgetUser.contains here but for the type of comparision I need, I would need
        // a misleading BudgetUser.equals method... I'm not so sure if that's useful.
        for (UniversityCostSharePersonnel universityCostSharePerson : universityCostSharePersonnelCopy) {
            boolean found = false;

            for (BudgetUser person : personnel) {
                if (universityCostSharePerson.getChartOfAccountsCode().equals(person.getFiscalCampusCode()) && universityCostSharePerson.getOrganizationCode().equals(person.getPrimaryDepartmentCode())) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                universityCostSharePersonnel.remove(universityCostSharePerson);
            }
        }
    }

    public void reconcileCostShare(String researchDocumentNumber, List<BudgetUser> personnel, List<UniversityCostSharePersonnel> universityCostSharePersonnel) {
        for (BudgetUser person : personnel) {
            UniversityCostSharePersonnel universityCostSharePerson = new UniversityCostSharePersonnel();
            universityCostSharePerson.setResearchDocumentNumber(researchDocumentNumber);
            universityCostSharePerson.setChartOfAccountsCode(StringUtils.defaultString(person.getFiscalCampusCode()));
            universityCostSharePerson.setOrganizationCode(StringUtils.defaultString(person.getPrimaryDepartmentCode()));

            // Check if it already is in list, if it is, don't add. It will also check that chart & org are set. That is important
            // because TO BE NAMED people may not have a chart / org, we don't want to add those at this time.
            if (!ObjectUtils.collectionContainsObjectWithIdentitcalKey(universityCostSharePersonnel, universityCostSharePerson) && !StringUtils.isEmpty(universityCostSharePerson.getChartOfAccountsCode()) && !StringUtils.isEmpty(universityCostSharePerson.getOrganizationCode())) {
                universityCostSharePersonnel.add(universityCostSharePerson);
            }
        }
    }
}
