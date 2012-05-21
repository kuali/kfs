/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.bc.document.web.struts;

import org.kuali.kfs.module.bc.businessobject.BudgetConstructionMonthly;
import org.kuali.kfs.module.bc.document.service.BenefitsCalculationService;
import org.kuali.kfs.module.bc.document.service.SalarySettingService;
import org.kuali.kfs.sys.context.SpringContext;

public class MonthlyBudgetForm extends BudgetExpansionForm {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MonthlyBudgetForm.class);

    private BudgetConstructionMonthly budgetConstructionMonthly;
    private boolean monthlyPersisted;
    private boolean monthlyReadOnly = true;
    private boolean budgetableDocument = false;

    private boolean hideDetails = false;

    // url parameters sent from BCDoc
    private String documentNumber;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String financialBalanceTypeCode;
    private String financialObjectTypeCode;
    
    private boolean revenue = false;

    public MonthlyBudgetForm() {
        super();
        setBudgetConstructionMonthly(new BudgetConstructionMonthly());
    }

    /**
     * Gets the budgetConstructioMonthly attribute.
     * 
     * @return Returns the budgetConstructioMonthly.
     */
    public BudgetConstructionMonthly getBudgetConstructionMonthly() {
        return budgetConstructionMonthly;
    }

    /**
     * Sets the budgetConstructioMonthly attribute value.
     * 
     * @param budgetConstructioMonthly The budgetConstructioMonthly to set.
     */
    public void setBudgetConstructionMonthly(BudgetConstructionMonthly budgetConstructionMonthly) {
        this.budgetConstructionMonthly = budgetConstructionMonthly;
    }

    /**
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute value.
     * 
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute value.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the financialBalanceTypeCode attribute.
     * 
     * @return Returns the financialBalanceTypeCode.
     */
    public String getFinancialBalanceTypeCode() {
        return financialBalanceTypeCode;
    }

    /**
     * Sets the financialBalanceTypeCode attribute value.
     * 
     * @param financialBalanceTypeCode The financialBalanceTypeCode to set.
     */
    public void setFinancialBalanceTypeCode(String financialBalanceTypeCode) {
        this.financialBalanceTypeCode = financialBalanceTypeCode;
    }

    /**
     * Gets the financialObjectCode attribute.
     * 
     * @return Returns the financialObjectCode.
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode attribute value.
     * 
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * Gets the financialObjectTypeCode attribute.
     * 
     * @return Returns the financialObjectTypeCode.
     */
    public String getFinancialObjectTypeCode() {
        return financialObjectTypeCode;
    }

    /**
     * Sets the financialObjectTypeCode attribute value.
     * 
     * @param financialObjectTypeCode The financialObjectTypeCode to set.
     */
    public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
        this.financialObjectTypeCode = financialObjectTypeCode;
    }

    /**
     * Gets the financialSubObjectCode attribute.
     * 
     * @return Returns the financialSubObjectCode.
     */
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * Sets the financialSubObjectCode attribute value.
     * 
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    /**
     * Gets the subAccountNumber attribute.
     * 
     * @return Returns the subAccountNumber.
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber attribute value.
     * 
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * Gets the monthlyPersisted attribute.
     * 
     * @return Returns the monthlyPersisted.
     */
    public boolean isMonthlyPersisted() {
        return monthlyPersisted;
    }

    /**
     * Sets the monthlyPersisted attribute value.
     * 
     * @param monthlyPersisted The monthlyPersisted to set.
     */
    public void setMonthlyPersisted(boolean monthlyPersisted) {
        this.monthlyPersisted = monthlyPersisted;
    }

    /**
     * Gets the readOnly attribute.
     * 
     * @return Returns the readOnly.
     */
    public boolean isMonthlyReadOnly() {
        return monthlyReadOnly;
    }

    /**
     * Sets the readOnly attribute value.
     * 
     * @param readOnly The readOnly to set.
     */
    public void setMonthlyReadOnly(boolean readOnly) {
        this.monthlyReadOnly = readOnly;
    }

    /**
     * Gets the budgetableDocument attribute.
     * 
     * @return Returns the budgetableDocument.
     */
    public boolean isBudgetableDocument() {
        return budgetableDocument;
    }

    /**
     * Sets the budgetableDocument attribute value.
     * 
     * @param budgetableDocument The budgetableDocument to set.
     */
    public void setBudgetableDocument(boolean budgetableDocument) {
        this.budgetableDocument = budgetableDocument;
    }

    /**
     * Gets the benefitsCalculationDisabled attribute.
     * 
     * @return Returns the benefitsCalculationDisabled.
     */
    public boolean isBenefitsCalculationDisabled() {
        return SpringContext.getBean(BenefitsCalculationService.class).isBenefitsCalculationDisabled();
    }

    /**
     * Gets the salarySettingDisabled attribute.
     * 
     * @return Returns the salarySettingDisabled.
     */
    public boolean isSalarySettingDisabled() {
        return SpringContext.getBean(SalarySettingService.class).isSalarySettingDisabled();
    }

    /**
     * Gets the hideDetails attribute.
     * 
     * @return Returns the hideDetails.
     */
    public boolean isHideDetails() {
        return hideDetails;
    }

    /**
     * Sets the hideDetails attribute value.
     * 
     * @param hideDetails The hideDetails to set.
     */
    public void setHideDetails(boolean hideDetails) {
        this.hideDetails = hideDetails;
    }

    /**
     * Gets the revenue attribute.
     * 
     * @return Returns the revenue
     */
    
    public boolean isRevenue() {
        return revenue;
    }

    /**	
     * Sets the revenue attribute.
     * 
     * @param revenue The revenue to set.
     */
    public void setRevenue(boolean revenue) {
        this.revenue = revenue;
    }

}
