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
