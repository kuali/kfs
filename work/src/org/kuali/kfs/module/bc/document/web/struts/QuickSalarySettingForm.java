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
package org.kuali.kfs.module.bc.document.web.struts;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.businessobject.SalarySettingExpansion;
import org.kuali.kfs.module.bc.document.authorization.BudgetConstructionDocumentAuthorizer;
import org.kuali.kfs.sys.KFSPropertyConstants;


public class QuickSalarySettingForm extends BudgetExpansionForm {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(QuickSalarySettingForm.class);

    private SalarySettingExpansion salarySettingExpansion;

    // TODO probably need to push these and some url parms to new superclass BCExpansionForm??
    private boolean hideDetails = false;
    protected Map editingMode;

    // url parameters sent from BCDoc
    private Integer universityFiscalYear;
    private String documentNumber;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String financialBalanceTypeCode;
    private String financialObjectTypeCode;

    private boolean hideAdjustmentMeasurement = true;
    private String adjustmentMeasurement;
    private KualiDecimal adjustmentAmount;

    public QuickSalarySettingForm() {
        super();
        LOG.debug("creating SalarySettingForm");
        setSalarySettingExpansion(new SalarySettingExpansion());
    }

    /**
     * @see org.kuali.core.web.struts.form.KualiForm#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);
        
        Enumeration names = request.getAttributeNames();
        while(names.hasMoreElements()) {
            String name = (String)names.nextElement();
            LOG.info(name + ": " + request.getAttribute(name));
        }

        this.populateBCAFLines();

        this.populateAuthorizationFields(new BudgetConstructionDocumentAuthorizer());
    }

    /**
     * This method iterates over all of the BCAF lines for the SalarySettingExpansion (PBGL line) TODO verify this - and calls
     * prepareAccountingLineForValidationAndPersistence on each one. This is called to refresh ref objects for use by validation
     */
    public void populateBCAFLines() {
        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = this.salarySettingExpansion.getPendingBudgetConstructionAppointmentFunding();
        for(PendingBudgetConstructionAppointmentFunding appointmentFunding : appointmentFundings) {
            this.populateBCAFLine(appointmentFunding);
        }
    }

    /**
     * Populates the dependent fields of objects contained within the BCAF line
     */
    private void populateBCAFLine(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        appointmentFunding.refreshNonUpdateableReferences();
        appointmentFunding.refreshReferenceObject(BCPropertyConstants.BUDGET_CONSTRUCTION_INTENDED_INCUMBENT);
        appointmentFunding.refreshReferenceObject(BCPropertyConstants.BUDGET_CONSTRUCTION_POSITION);
        appointmentFunding.refreshReferenceObject(BCPropertyConstants.BUDGET_CONSTRUCTION_CALCULATED_SALARY_FOUNDATION_TRACKER);
    }

    /**
     * Updates authorization-related form fields based on the current form contents
     */
    public void populateAuthorizationFields(BudgetConstructionDocumentAuthorizer documentAuthorizer) {

        useBCAuthorizer(documentAuthorizer);

        // TODO probably need BCAuthorizationConstants extension
        if (getEditingMode().containsKey(AuthorizationConstants.EditMode.UNVIEWABLE)) {
            throw new AuthorizationException(GlobalVariables.getUserSession().getUniversalUser().getPersonName(), "view", this.getAccountNumber() + ", " + this.getSubAccountNumber());
        }

        /*
         * //TODO from KualiDocumentFormBase - remove when ready if (isFormDocumentInitialized()) {
         * useBCAuthorizer(documentAuthorizer); // graceless hack which takes advantage of the fact that here and only here will we
         * have guaranteed access to the // correct DocumentAuthorizer if
         * (getEditingMode().containsKey(AuthorizationConstants.EditMode.UNVIEWABLE)) { throw new
         * AuthorizationException(GlobalVariables.getUserSession().getUniversalUser().getPersonName(), "view",
         * this.getAccountNumber()+", "+this.getSubAccountNumber()); } }
         */
    }

    protected void useBCAuthorizer(BudgetConstructionDocumentAuthorizer documentAuthorizer) {
        UniversalUser kualiUser = GlobalVariables.getUserSession().getUniversalUser();

        setEditingMode(documentAuthorizer.getEditMode(this.getUniversityFiscalYear(), this.getChartOfAccountsCode(), this.getAccountNumber(), this.getSubAccountNumber(), kualiUser));
        // TODO probably don't need these, editingmode drives expansion screen actions
        // setDocumentActionFlags(documentAuthorizer.getDocumentActionFlags(document, kualiUser));
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
     * Gets the editingMode attribute.
     * 
     * @return Returns the editingMode.
     */
    public Map getEditingMode() {
        return editingMode;
    }

    /**
     * Sets the editingMode attribute value.
     * 
     * @param editingMode The editingMode to set.
     */
    public void setEditingMode(Map editingMode) {
        this.editingMode = editingMode;
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
     * Gets the salarySettingExpansion attribute.
     * 
     * @return Returns the salarySettingExpansion.
     */
    public SalarySettingExpansion getSalarySettingExpansion() {
        return salarySettingExpansion;
    }

    /**
     * Sets the salarySettingExpansion attribute value.
     * 
     * @param salarySettingExpansion The salarySettingExpansion to set.
     */
    public void setSalarySettingExpansion(SalarySettingExpansion salarySettingExpansion) {
        this.salarySettingExpansion = salarySettingExpansion;
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
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear.
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute value.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the adjustmentMeasurement attribute.
     * 
     * @return Returns the adjustmentMeasurement.
     */
    public String getAdjustmentMeasurement() {
        return adjustmentMeasurement;
    }

    /**
     * Sets the adjustmentMeasurement attribute value.
     * 
     * @param adjustmentMeasurement The adjustmentMeasurement to set.
     */
    public void setAdjustmentMeasurement(String adjustmentMeasurement) {
        this.adjustmentMeasurement = adjustmentMeasurement;
    }

    /**
     * Gets the adjustmentAmount attribute.
     * 
     * @return Returns the adjustmentAmount.
     */
    public KualiDecimal getAdjustmentAmount() {
        return adjustmentAmount;
    }

    /**
     * Sets the adjustmentAmount attribute value.
     * 
     * @param adjustmentAmount The adjustmentAmount to set.
     */
    public void setAdjustmentAmount(KualiDecimal adjustmentAmount) {
        this.adjustmentAmount = adjustmentAmount;
    }

    /**
     * Gets the hideAdjustmentMeasurement attribute.
     * 
     * @return Returns the hideAdjustmentMeasurement.
     */
    public boolean isHideAdjustmentMeasurement() {
        return hideAdjustmentMeasurement;
    }

    /**
     * Sets the hideAdjustmentMeasurement attribute value.
     * 
     * @param hideAdjustmentMeasurement The hideAdjustmentMeasurement to set.
     */
    public void setHideAdjustmentMeasurement(boolean hideAdjustmentMeasurement) {
        this.hideAdjustmentMeasurement = hideAdjustmentMeasurement;
    }
    
    public Map<String, Object> getKeyMapOfSalarySettingExpension(){
        Map<String, Object> keyMap = new HashMap<String, Object>();
        
        keyMap.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.getDocumentNumber());
        keyMap.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, this.getUniversityFiscalYear());
        keyMap.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, this.getChartOfAccountsCode());
        keyMap.put(KFSPropertyConstants.ACCOUNT_NUMBER, this.getAccountNumber());
        keyMap.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, this.getSubAccountNumber());
        keyMap.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, this.getFinancialObjectCode());
        keyMap.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, this.getFinancialSubObjectCode());
        keyMap.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, this.getFinancialBalanceTypeCode());
        keyMap.put(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, this.getFinancialObjectTypeCode());
        
        return keyMap;
    }
}
