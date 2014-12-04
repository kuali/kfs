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

/**
 * Wraps an OrganizationReversion detail to make sure that organization reversion returns only 
 * Closed Account details, which in turn will force R2 logic to run for all the details
 */
public class ClosedAccountOrganizationReversion implements CarryForwardReversionProcessOrganizationInfo {
    private CarryForwardReversionProcessOrganizationInfo organizationReversion;
    
    /**
     * Constructs a ClosedAccountOrganizationReversion
     * @param organizationReversion the organization reversion to wrap
     */
    public ClosedAccountOrganizationReversion(CarryForwardReversionProcessOrganizationInfo organizationReversion) {
        this.organizationReversion = organizationReversion;
    }

    /**
     * Returns the budget reversion account number from the wrapped org reversion
     * @see org.kuali.kfs.coa.businessobject.CarryForwardReversionProcessOrganizationInfo#getBudgetReversionAccountNumber()
     */
    public String getBudgetReversionAccountNumber() {
        return organizationReversion.getBudgetReversionAccountNumber();
    }

    /**
     * Returns the cash reversion account number from the wrapped org reversion
     * @see org.kuali.kfs.coa.businessobject.CarryForwardReversionProcessOrganizationInfo#getCashReversionAccountNumber()
     */
    public String getCashReversionAccountNumber() {
        return organizationReversion.getCashReversionAccountNumber();
    }

    /**
     * Returns the chart of accounts code from the wrapped org reversion
     * @see org.kuali.kfs.coa.businessobject.CarryForwardReversionProcessOrganizationInfo#getChartOfAccountsCode()
     */
    public String getChartOfAccountsCode() {
        return organizationReversion.getChartOfAccountsCode();
    }

    /**
     * Returns the organization code from the wrapped org reversion
     * @see org.kuali.kfs.coa.businessobject.CarryForwardReversionProcessOrganizationInfo#getOrganizationCode()
     */
    public String getOrganizationCode() {
        return organizationReversion.getOrganizationCode();
    }

    /**
     * Returns a closed account org reversion detail for the given category
     * @see org.kuali.kfs.coa.businessobject.CarryForwardReversionProcessOrganizationInfo#getOrganizationReversionDetail(java.lang.String)
     */
    public OrganizationReversionCategoryInfo getOrganizationReversionDetail(String categoryCode) {
        OrganizationReversionCategoryInfo orgReversionDetail = organizationReversion.getOrganizationReversionDetail(categoryCode);
        if (orgReversionDetail != null) {
            return new ClosedAccountOrganizationReversionDetail(orgReversionDetail);
        } else {
            return null;
        }
    }

    /**
     * Returns the fiscal year from the wrapped org reversion
     * @see org.kuali.kfs.coa.businessobject.CarryForwardReversionProcessOrganizationInfo#getUniversityFiscalYear()
     */
    public Integer getUniversityFiscalYear() {
        return organizationReversion.getUniversityFiscalYear();
    }

    /**
     * Returns the carry forward by object code indicator from the wrapped org reversion
     * @see org.kuali.kfs.coa.businessobject.CarryForwardReversionProcessOrganizationInfo#isCarryForwardByObjectCodeIndicator()
     */
    public boolean isCarryForwardByObjectCodeIndicator() {
        return organizationReversion.isCarryForwardByObjectCodeIndicator();
    }

    /**
     * returns the budget reversion chart of accounts code from the wrapped organization reversion
     * @see org.kuali.kfs.coa.businessobject.CarryForwardReversionProcessOrganizationInfo#getBudgetReversionChartOfAccountsCode()
     */
    public String getBudgetReversionChartOfAccountsCode() {
        return organizationReversion.getBudgetReversionChartOfAccountsCode();
    }

    /**
     * returns the cash reversion chart cash object code from the wrapped organization reversion
     * @see org.kuali.kfs.coa.businessobject.CarryForwardReversionProcessOrganizationInfo#getCashReversionChartCashObjectCode()
     */
    public String getCashReversionChartCashObjectCode() {
        return organizationReversion.getCashReversionChartCashObjectCode();
    }

    /**
     * returns the cash reversion chart of accounts code from the wrapped organization reversion
     * @see org.kuali.kfs.coa.businessobject.CarryForwardReversionProcessOrganizationInfo#getCashReversionFinancialChartOfAccountsCode()
     */
    public String getCashReversionFinancialChartOfAccountsCode() {
        return organizationReversion.getCashReversionFinancialChartOfAccountsCode();
    }

    /**
     * returns the organization chart's cash object code from the wrapped organization reversion
     * @see org.kuali.kfs.coa.businessobject.CarryForwardReversionProcessOrganizationInfo#getOrganizationChartCashObjectCode()
     */
    public String getOrganizationChartCashObjectCode() {
        return organizationReversion.getOrganizationChartCashObjectCode();
    }
}
