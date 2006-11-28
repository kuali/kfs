/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/cg/businessobject/BudgetInstitutionCostShare.java,v $
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

package org.kuali.module.kra.budget.bo;

import java.util.List;

/**
 * 
 */
public class BudgetInstitutionCostShare extends BudgetAbstractCostShare {

    private String organizationCode;
    private String chartOfAccountsCode;
    private boolean permissionIndicator;

    public BudgetInstitutionCostShare() {
        super();
    }

    public BudgetInstitutionCostShare(BudgetInstitutionCostShare budgetInstitutionCostShare) {
        this.documentNumber = budgetInstitutionCostShare.getDocumentNumber();
        this.budgetCostShareSequenceNumber = budgetInstitutionCostShare.getBudgetCostShareSequenceNumber();
        this.organizationCode = budgetInstitutionCostShare.getOrganizationCode();
        this.chartOfAccountsCode = budgetInstitutionCostShare.getChartOfAccountsCode();
        this.budgetCostShareDescription = budgetInstitutionCostShare.getBudgetCostShareDescription();
        this.budgetPeriodCostShare = budgetInstitutionCostShare.getBudgetPeriodCostShare();
        this.permissionIndicator = budgetInstitutionCostShare.isPermissionIndicator();
    }

    /**
     * Gets the organizationCode attribute.
     * 
     * @return Returns the organizationCode
     * 
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * Sets the organizationCode attribute.
     * 
     * @param organizationCode The organizationCode to set.
     * 
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the documentchartOfAccountsCodeHeaderId
     * 
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     * 
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the budgetPeriodInstitutionCostShare attribute.
     * 
     * @return Returns the budgetPeriodInstitutionCostShare
     * 
     */
    public List<BudgetPeriodInstitutionCostShare> getBudgetPeriodCostShare() {
        return budgetPeriodCostShare;
    }

    public BudgetPeriodInstitutionCostShare getBudgetPeriodCostShareItem(int index) {
        while (getBudgetPeriodCostShare().size() <= index) {
            getBudgetPeriodCostShare().add(new BudgetPeriodInstitutionCostShare());
        }
        return (BudgetPeriodInstitutionCostShare) getBudgetPeriodCostShare().get(index);
    }

    /**
     * Sets the budgetPeriodInstitutionCostShare attribute.
     * 
     * @param budgetPeriodInstitutionCostShare The budgetPeriodInstitutionCostShare to set.
     * 
     */
    public void setBudgetPeriodCostShare(List budgetPeriodCostShare) {
        this.budgetPeriodCostShare = budgetPeriodCostShare;
    }

    /**
     * Gets the permissionIndicator attribute.
     * 
     * @return - Returns the permissionIndicator
     */
    public boolean isPermissionIndicator() {
        return permissionIndicator;
    }

    /**
     * Sets the permissionIndicator attribute.
     * 
     * @param permissionIndicator The permissionIndicator to set.
     */
    public void setPermissionIndicator(boolean permissionIndicator) {
        this.permissionIndicator = permissionIndicator;
    }
}