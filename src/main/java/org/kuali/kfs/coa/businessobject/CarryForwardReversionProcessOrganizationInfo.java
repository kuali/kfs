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
