/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.gl.bo;

import java.util.LinkedHashMap;

import org.kuali.core.util.KualiDecimal;
import org.kuali.core.bo.PersistableBusinessObjectBase;

public class OrgReversionUnitOfWorkCategoryAmount extends PersistableBusinessObjectBase {
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String categoryCode;
    private KualiDecimal actual = KualiDecimal.ZERO;
    private KualiDecimal budget = KualiDecimal.ZERO;
    private KualiDecimal encumbrance = KualiDecimal.ZERO;
    private KualiDecimal carryForward = KualiDecimal.ZERO;
    private KualiDecimal available = KualiDecimal.ZERO;

    public OrgReversionUnitOfWorkCategoryAmount(String chartOfAccountsCode, String accountNbr, String subAccountNbr, String cat) {
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.accountNumber = accountNbr;
        this.subAccountNumber = subAccountNbr;
        categoryCode = cat;
    }

    public void addActual(KualiDecimal amount) {
        actual = actual.add(amount);
    }

    public void addBudget(KualiDecimal amount) {
        budget = budget.add(amount);
    }

    public void addEncumbrance(KualiDecimal amount) {
        encumbrance = encumbrance.add(amount);
    }

    public void addCarryForward(KualiDecimal amount) {
        carryForward = carryForward.add(amount);
    }

    public void addAvailable(KualiDecimal amount) {
        available = available.add(amount);
    }

    public KualiDecimal getAvailable() {
        return available;
    }

    public void setAvailable(KualiDecimal available) {
        this.available = available;
    }

    public KualiDecimal getActual() {
        return actual;
    }

    public void setActual(KualiDecimal actual) {
        this.actual = actual;
    }

    public KualiDecimal getBudget() {
        return budget;
    }

    public void setBudget(KualiDecimal budget) {
        this.budget = budget;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public KualiDecimal getEncumbrance() {
        return encumbrance;
    }

    public void setEncumbrance(KualiDecimal encumbrance) {
        this.encumbrance = encumbrance;
    }

    public KualiDecimal getCarryForward() {
        return carryForward;
    }

    public void setCarryForward(KualiDecimal carryForward) {
        this.carryForward = carryForward;
    }

    /**
     * Gets the accountNbr attribute. 
     * @return Returns the accountNbr.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNbr attribute value.
     * @param accountNbr The accountNbr to set.
     */
    public void setAccountNumber(String accountNbr) {
        this.accountNumber = accountNbr;
    }

    /**
     * Gets the chartOfAccountsCode attribute. 
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute value.
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the subAccountNbr attribute. 
     * @return Returns the subAccountNbr.
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNbr attribute value.
     * @param subAccountNbr The subAccountNbr to set.
     */
    public void setSubAccountNumber(String subAccountNbr) {
        this.subAccountNumber = subAccountNbr;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap pkMap = new LinkedHashMap();
        pkMap.put("chartOfAccountsCode", this.chartOfAccountsCode);
        pkMap.put("accountNbr", this.accountNumber);
        pkMap.put("subAccountNbr", this.subAccountNumber);
        pkMap.put("categoryCode", this.categoryCode);
        return pkMap;
    }
    
}
