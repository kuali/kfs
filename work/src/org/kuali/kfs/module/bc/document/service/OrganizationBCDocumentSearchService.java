/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.budget.service;

/**
 * This class defines methods an OrganizationBCDocumentSearchService must provide The OrganizationBCDocumentSearchService provides
 * methods that build and clean the temporary tables used in the Organization Account (Budget Document) Selection lookups. It is
 * assumed that the Organization Tree exists and that the user has selected at least one organization before deriving the list of
 * budgeted accounts (Budget Documents).
 */
public interface OrganizationBCDocumentSearchService {

    /**
     * This method populates BudgetConstructionAccountSelect with rows associated with a set of selected organizations in the
     * Organization Tree for the user and where Budget Documents exist.
     * 
     * @param personUserIdentifier
     * @param universityFiscalYear
     */
    public int buildAccountSelectPullList(String personUserIdentifier, Integer universityFiscalYear);

    public void buildBudgetedAccountsAbovePointsOfView(String personUserIdentifier, Integer universityFiscalYear, String chartOfAccountsCode, String organizationCode);
    
    /**
     * This method depopulates BudgetConstructionAccountSelect rows associated with the user.
     * 
     * @param personUserIdentifier
     * @param universityFiscalYear
     */
    public void cleanAccountSelectPullList(String personUserIdentifier, Integer universityFiscalYear);
}
