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
package org.kuali.kfs.coa.businessobject;

public interface CarryForwardReversionProcessOrganizationInfo {
    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode
     */
    public abstract String getChartOfAccountsCode();
    
    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return Returns the chartOfAccounts
     */
    //public abstract Chart getChartOfAccounts();
    
    /**
     * Gets the organizationCode attribute.
     * 
     * @return Returns the organizationCode
     */
    public abstract String getOrganizationCode();
    
    /**
     * Gets the carryForwardByObjectCodeIndicator attribute.
     * 
     * @return Returns the carryForwardByObjectCodeIndicator
     */
    public abstract boolean isCarryForwardByObjectCodeIndicator();
    
    /**
     * Gets the cashReversionFinancialChartOfAccountsCode attribute.
     * 
     * @return Returns the cashReversionFinancialChartOfAccountsCode
     */
    public abstract String getCashReversionFinancialChartOfAccountsCode();
    
    /**
     * Gets the cashReversionAccountNumber attribute.
     * 
     * @return Returns the cashReversionAccountNumber
     */
    public abstract String getCashReversionAccountNumber();
    
    /**
     * Retrieves an organization reversion detail by category code
     * @param categoryCode category code to find detail for
     * @return the organization reversion detail, or null if no active detail record for the given category code could be found
     */
    public OrganizationReversionCategoryInfo getOrganizationReversionDetail(String categoryCode);
    
    /**
     * Gets the budgetReversionChartOfAccountsCode attribute.
     * 
     * @return Returns the budgetReversionChartOfAccountsCode
     */
    public abstract String getBudgetReversionChartOfAccountsCode();
    
    /**
     * Gets the budgetReversionAccountNumber attribute.
     * 
     * @return Returns the budgetReversionAccountNumber
     */
    public abstract String getBudgetReversionAccountNumber();
    
    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear
     */
    public abstract Integer getUniversityFiscalYear();
    
    /**
     * @return the cash object code from the cash reversion's chart
     */
    public abstract String getCashReversionChartCashObjectCode();
    
    /**
     * @return the cash object code from the organization's chart
     */
    public abstract String getOrganizationChartCashObjectCode();
}
