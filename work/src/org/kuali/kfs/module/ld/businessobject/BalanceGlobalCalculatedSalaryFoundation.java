/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.kfs.module.ld.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * Labor business object for Balance Global Calculated Salary Foundation.
 */
public class BalanceGlobalCalculatedSalaryFoundation extends PersistableBusinessObjectBase {

    private String principalId;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private KualiDecimal budgetAmount;
    private KualiDecimal calculatedSalaryFoundationAmount;

    /**
     * Default constructor.
     */
    public BalanceGlobalCalculatedSalaryFoundation() {

    }

    /**
     * Gets the principalId attribute.
     * 
     * @return Returns the principalId
     */
    public String getPrincipalId() {
        return principalId;
    }

    /**
     * Sets the principalId attribute.
     * 
     * @param principalId The principalId to set.
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    /**
     * Gets the subAccountNumber attribute.
     * 
     * @return Returns the subAccountNumber
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber attribute.
     * 
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * Gets the financialObjectCode attribute.
     * 
     * @return Returns the financialObjectCode
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode attribute.
     * 
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * Gets the financialSubObjectCode attribute.
     * 
     * @return Returns the financialSubObjectCode
     */
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * Sets the financialSubObjectCode attribute.
     * 
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    /**
     * Gets the budgetAmount attribute.
     * 
     * @return Returns the budgetAmount
     */
    public KualiDecimal getBudgetAmount() {
        return budgetAmount;
    }

    /**
     * Sets the budgetAmount attribute.
     * 
     * @param budgetAmount The budgetAmount to set.
     */
    public void setBudgetAmount(KualiDecimal budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    /**
     * Gets the calculatedSalaryFoundationAmount attribute.
     * 
     * @return Returns the calculatedSalaryFoundationAmount
     */
    public KualiDecimal getCalculatedSalaryFoundationAmount() {
        return calculatedSalaryFoundationAmount;
    }

    /**
     * Sets the calculatedSalaryFoundationAmount attribute.
     * 
     * @param calculatedSalaryFoundationAmount The calculatedSalaryFoundationAmount to set.
     */
    public void setCalculatedSalaryFoundationAmount(KualiDecimal calculatedSalaryFoundationAmount) {
        this.calculatedSalaryFoundationAmount = calculatedSalaryFoundationAmount;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("principalId", this.principalId);
        m.put("subAccountNumber", this.subAccountNumber);
        m.put("financialObjectCode", this.financialObjectCode);
        m.put("financialSubObjectCode", this.financialSubObjectCode);

        return m;
    }
}

