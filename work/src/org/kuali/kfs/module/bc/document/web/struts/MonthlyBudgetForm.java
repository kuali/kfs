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
package org.kuali.module.budget.web.struts.form;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.module.budget.bo.BudgetConstructionMonthly;

public class MonthlyBudgetForm extends KualiForm {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MonthlyBudgetForm.class);
    
    private BudgetConstructionMonthly budgetConstructionMonthly;
//  TODO remove - was here originally for kul:page tag use 
//    private String docTypeName;

    //TODO probably need to push this to new superclass BCExpansionForm??
    private boolean hideDetails = false;

    // url parameters sent from BCDoc
    private String returnAnchor;
    private String returnFormKey;
    private String documentNumber;
    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String financialBalanceTypeCode;
    private String financialObjectTypeCode;

    public MonthlyBudgetForm(){
        super();
        setBudgetConstructionMonthly(new BudgetConstructionMonthly());
        getBudgetConstructionMonthly().setDocumentNumber("1234");

// TODO remove - was here originally for kul:page tag use 
//      this.setDocTypeName("KualiBudgetConstructionDocument");
    }

    /**
     * Gets the budgetConstructioMonthly attribute. 
     * @return Returns the budgetConstructioMonthly.
     */
    public BudgetConstructionMonthly getBudgetConstructionMonthly() {
        return budgetConstructionMonthly;
    }

    /**
     * Sets the budgetConstructioMonthly attribute value.
     * @param budgetConstructioMonthly The budgetConstructioMonthly to set.
     */
    public void setBudgetConstructionMonthly(BudgetConstructionMonthly budgetConstructionMonthly) {
        this.budgetConstructionMonthly = budgetConstructionMonthly;
    }

    /**
     * Gets the docTypeName attribute. 
     * @return Returns the docTypeName.
     */
//  TODO remove - was here originally for kul:page tag use 
//    public String getDocTypeName() {
//        return docTypeName;
//    }

    /**
     * Sets the docTypeName attribute value.
     * @param docTypeName The docTypeName to set.
     */
//  TODO remove - was here originally for kul:page tag use 
//    public void setDocTypeName(String docTypeName) {
//        this.docTypeName = docTypeName;
//    }

    /**
     * Gets the returnAnchor attribute. 
     * @return Returns the returnAnchor.
     */
    public String getReturnAnchor() {
        return returnAnchor;
    }

    /**
     * Sets the returnAnchor attribute value.
     * @param returnAnchor The returnAnchor to set.
     */
    public void setReturnAnchor(String returnAnchor) {
        this.returnAnchor = returnAnchor;
    }

    /**
     * Gets the returnTabStates attribute. 
     * @return Returns the returnTabStates.
     */
    public String getReturnFormKey() {
        return returnFormKey;
    }

    /**
     * Sets the returnTabStates attribute value.
     * @param returnTabStates The returnTabStates to set.
     */
    public void setReturnFormKey(String returnTabStates) {
        this.returnFormKey = returnTabStates;
    }

    /**
     * Gets the accountNumber attribute. 
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute value.
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
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
     * Gets the documentNumber attribute. 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the financialBalanceTypeCode attribute. 
     * @return Returns the financialBalanceTypeCode.
     */
    public String getFinancialBalanceTypeCode() {
        return financialBalanceTypeCode;
    }

    /**
     * Sets the financialBalanceTypeCode attribute value.
     * @param financialBalanceTypeCode The financialBalanceTypeCode to set.
     */
    public void setFinancialBalanceTypeCode(String financialBalanceTypeCode) {
        this.financialBalanceTypeCode = financialBalanceTypeCode;
    }

    /**
     * Gets the financialObjectCode attribute. 
     * @return Returns the financialObjectCode.
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode attribute value.
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * Gets the financialObjectTypeCode attribute. 
     * @return Returns the financialObjectTypeCode.
     */
    public String getFinancialObjectTypeCode() {
        return financialObjectTypeCode;
    }

    /**
     * Sets the financialObjectTypeCode attribute value.
     * @param financialObjectTypeCode The financialObjectTypeCode to set.
     */
    public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
        this.financialObjectTypeCode = financialObjectTypeCode;
    }

    /**
     * Gets the financialSubObjectCode attribute. 
     * @return Returns the financialSubObjectCode.
     */
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * Sets the financialSubObjectCode attribute value.
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    /**
     * Gets the subAccountNumber attribute. 
     * @return Returns the subAccountNumber.
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber attribute value.
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * Gets the universityFiscalYear attribute. 
     * @return Returns the universityFiscalYear.
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute value.
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the hideDetails attribute. 
     * @return Returns the hideDetails.
     */
    public boolean isHideDetails() {
        return hideDetails;
    }

    /**
     * Sets the hideDetails attribute value.
     * @param hideDetails The hideDetails to set.
     */
    public void setHideDetails(boolean hideDetails) {
        this.hideDetails = hideDetails;
    }
    
}
