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
package org.kuali.module.kra.budget.service;

import java.util.List;

import org.kuali.module.kra.budget.bo.BudgetThirdPartyCostShare;
import org.kuali.module.kra.budget.bo.BudgetInstitutionCostShare;
import org.kuali.module.kra.budget.bo.BudgetUser;
import org.kuali.module.kra.budget.bo.InstitutionCostSharePersonnel;

/**
 * 
 * This interface defines methods that a BudgetCostShareService must provide.
 * 
 * 
 */
public interface BudgetCostShareService {

    /**
     * This method will remove institutionCostSharePersonnel entries that no longer have a personnel entry. It will also remove the
     * entire budgetInstitutionCostShare list if institutionCostShareIndicator is not set anymore. The same goes for
     * budgetThirdPartyCostShare and budgetThirdPartyCostShareIndicator.
     * 
     * @param institutionCostShareIndicator if budgetInstitutionCostShare should be removed
     * @param budgetInstitutionCostShare
     * @param budgetThirdPartyCostShareIndicator if budgetThirdPartyCostShare should be removed
     * @param budgetThirdPartyCostShare
     * @param personnel list of personnel to check for chart / orgs
     * @param institutionCostSharePersonnel chart / orgs to be removed if they arn't present in personnel list
     */
    public void cleanseCostShare(boolean institutionCostShareIndicator, List<BudgetInstitutionCostShare> budgetInstitutionCostShare, boolean budgetThirdPartyCostShareIndicator, List<BudgetThirdPartyCostShare> budgetThirdPartyCostShare, List<BudgetUser> personnel, List<InstitutionCostSharePersonnel> institutionCostSharePersonnel);

    /**
     * This method will add Institution Cost Share Personnel entries that have personnel entries if they don't already exist. It does
     * not add entries that don't have a chart or org set (this happens for TO BE NAMEDs).
     * 
     * @param documentNumber
     * @param personnel
     * @param institutionCostSharePersonnel
     */
    public void reconcileCostShare(String documentNumber, List<BudgetUser> personnel, List<InstitutionCostSharePersonnel> institutionCostSharePersonnel);
}
